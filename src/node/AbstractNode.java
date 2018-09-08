package node;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementation of a Generic Node.
 */
abstract class AbstractNode extends UnicastRemoteObject implements Node {
    final int id;
    final AtomicInteger messageCount;
    Node next;
    private boolean participant = false;
    private int leaderId;

    /**
     * Constructor for a generic node.
     *
     * @param id           The ID of this node
     * @param messageCount MessageCount is used to count the total number of messages sent during an election
     * @throws RemoteException If RMI fails to export the UnicastRemoteObject
     */
    AbstractNode(int id, AtomicInteger messageCount) throws RemoteException {
        super();
        this.id = id;
        this.messageCount = messageCount;
    }

    /**
     * By Synchronising calls to the following getter/setter methods, it affords us some level of ThreadSafety in a node
     * when several election rounds have been started concurrently.
     */
    synchronized boolean isParticipant() {
        return participant;
    }

    synchronized void setParticipant(boolean participant) {
        this.participant = participant;
    }

    synchronized int getLeaderId() {
        return leaderId;
    }

    synchronized void setLeaderId(int leaderId) {
        this.leaderId = leaderId;
    }

    public synchronized void setNext(Node next) throws RemoteException {
        this.next = next;
        System.out.println("Node " + id + " set its next value.");
    }

    /**
     * When called this node will trigger a new election round. A node can ony start an election if it isn't already
     * participating in one.
     *
     * @throws RemoteException If RMI fails during the election round
     */
    public void startElection() throws RemoteException {
        if (!isParticipant()) {
            output(String.format("Node %d started an election. StarterID: %d", id, id));
            setParticipant(true);
            next.sendElectionMessage(id, id);
        }
    }

    /**
     * Used to output the metrics we are gathering to text files.
     *
     * @param msg  The String to be written to the output
     */
    void output(final String msg) {
        try (FileWriter fw = new FileWriter(this.getClass().getSimpleName(), true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            System.out.println(msg);
            out.println(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
