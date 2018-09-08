package election;

import node.Node;

import java.rmi.RemoteException;

/**
 * Runnable class that allows several concurrent Client Elections to happen.
 */
public class ElectionRunnable implements Runnable {
    private Node node;

    ElectionRunnable(Node node) {
        this.node = node;
    }

    @Override
    public void run() {
        try {
            node.startElection();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
