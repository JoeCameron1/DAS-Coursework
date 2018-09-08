package node;

import java.rmi.RemoteException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implements original Chang and Roberts(ring leader election) algorithm.
 */
public class OriginalAlgorithm extends AbstractNode {
    /**
     * Constructor for a node which uses the original algorithm.
     *
     * @param id           The ID of this node
     * @param messageCount MessageCount is used to count the total number of messages sent during an election
     * @throws RemoteException If RMI fails to export the UnicastRemoteObject
     */
    public OriginalAlgorithm(int id, AtomicInteger messageCount) throws RemoteException {
        super(id, messageCount);
    }

    /**
     * Sends an election message to the next node in the ring.
     * If this node is not a participant in an election, then forward the election message onto the next node with the
     * suggested ID being the Max of this nodes ID and the current suggested ID specified in the received message.
     * Else If this node has been assigned to be the leader, then it decides it is the leader and sends an
     * Elected message to the next node.
     * Else If this node is already participating in an election and it's ID is lower than the current suggested ID,
     * then forward the message on with no changes.
     *
     * @param starterId The ID of the node which started the current election
     * @param leaderId  The ID of the current leader
     * @throws RemoteException If RMI fails while sending a message between two nodes
     */
    @Override
    public void sendElectionMessage(int starterId, int leaderId) throws RemoteException {
        output(String.format("Node %d received an election message. LeaderID: %d StarterID: %d",
                id, leaderId, starterId));
        messageCount.incrementAndGet();
        if (!isParticipant()) {
            setParticipant(true);
            next.sendElectionMessage(starterId, Math.max(leaderId, id));
        } else if (leaderId == id) {
            //election message has gone full circle
            setParticipant(false);
            setLeaderId(leaderId);
            output(String.format("Node %d decides leader is Node %d", id, getLeaderId()));
            next.sendElectedMessage(starterId, getLeaderId());
        } else {
            if (leaderId > id) {
                next.sendElectionMessage(starterId, leaderId);
            }
        }
    }

    /**
     * Sends an elected message to the next node.
     * If this node's ID is not the same as the starter ID, then send forward the elected message to the next node.
     * Else the elected message has gone full circle and this election round is finished.
     *
     * @param starterId The ID of the node which started the current election
     * @param electedId The ID of the node that has been elected to be the new leader
     * @throws RemoteException If RMI fails while sending a message between two nodes
     */
    @Override
    public void sendElectedMessage(int starterId, int electedId) throws RemoteException {
        output(String.format("Node %d received an elected message. ElectedID: %d StarterID: %d",
                id, electedId, starterId));
        messageCount.incrementAndGet();
        setLeaderId(electedId);
        setParticipant(false);
        //if starterId == id elected message has gone full circle
        if (getLeaderId() != id) {
            next.sendElectedMessage(starterId, getLeaderId());
        } else {
            output(String.format("Total number of messages sent: %d", messageCount.get()));
            messageCount.set(0);
        }
    }
}
