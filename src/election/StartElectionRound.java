package election;

import node.Node;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

/**
 * Calls to nodes with IDs specified as command-line arguments to each start an election. Elections are started in
 * separate threads to allow multiple concurrent elections to happen.
 *
 * args[0] Determines whether to lookup nodes implementing the augmented "-a" or original algorithm "-o"
 * args[1..] Specifies which nodes should start an election
 */
public class StartElectionRound {
    public static void main(String args[]) {
        String regHost = "localhost";
        Integer regPort = 1099;

        if (args.length == 0 || (!args[0].equals("-a") && !args[0].equals("-o"))) {
            System.err.println("Please specify which algorithm you are using.\n" +
                    "Use '-a' for augmented or '-o' for the original algorithm.");
            System.exit(1);
        }
        if (args.length == 1) {
            System.err.println("Please specify at least one node, by ID, to start an election.");
            System.exit(1);
        }

        Registry reg = null;
        try {
            reg = LocateRegistry.getRegistry(regHost, regPort);
        } catch (RemoteException e) {
            System.err.println("Couldn't find an RMI Registry at: " + regHost + ":" + regPort);
            System.exit(1);
        }

        ArrayList<Thread> electionThreads = new ArrayList<>();
        try {
            for (int i = 1; i < args.length; i++) {
                Node node = (Node) reg.lookup("Node" + args[0] + args[i]);
                electionThreads.add(new Thread(new ElectionRunnable(node)));
            }
        } catch (AccessException e) {
            System.err.println("Access to a RMI was denied.");
            e.printStackTrace();
        } catch (RemoteException e) {
            System.err.println("A remote exception occurred.");
            e.printStackTrace();
        } catch (NotBoundException e) {
            System.err.println("A lookup for a node failed because it had no associated binding.");
            e.printStackTrace();
        }

        long startTime = System.currentTimeMillis();
        try {
            for (Thread electionThread : electionThreads) {
                electionThread.start();
                sleep(100);
            }
        } catch (InterruptedException e) {
            System.err.println("Sleeping before starting subsequent elections was interrupted.");
            e.printStackTrace();
        }

        try {
            for (Thread electionThread : electionThreads) {
                electionThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long time = System.currentTimeMillis() - startTime;
        System.out.println("Time taken(ms): " + time);
    }
}
