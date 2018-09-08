package node;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Generic Node interface. A node that implements this interface can either be one that uses the original
 * algorithm, or one which uses our augmented algorithm.
 */
public interface Node extends Remote {

    /**
     * Used to pass an Election to the next node.
     *
     * @param starterId The ID of the node which started the current election
     * @param leaderId  The ID of the current leader
     * @throws RemoteException If RMI between two nodes fails while sending a message
     */
    void sendElectionMessage(int starterId, int leaderId) throws RemoteException;

    /**
     * Used to circulate an 'Elected' message between nodes
     *
     * @param starterId The ID of the node which started the current election
     * @param electedId The ID of the node that has been elected to be the new leader
     * @throws RemoteException If RMI between two nodes fails while sending a message
     */
    void sendElectedMessage(int starterId, int electedId) throws RemoteException;

    /**
     * Used to set the next node in the 'circle'. This is needed because we do not know what node will be 'next'
     * in the circle until it is created.
     *
     * @param next Node in the circle
     * @throws RemoteException If something goes wrong when setting the next node
     */
    void setNext(Node next) throws RemoteException;

    /**
     * A node that uses this method will trigger a new election round. Nodes which subsequently receive an
     * ElectionMessage will all follow either the original or augmented algorithm.
     *
     * @throws RemoteException If RMI fails during the election round
     */
    void startElection() throws RemoteException;
}
