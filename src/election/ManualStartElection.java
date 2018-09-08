package election;

import node.Node;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/* It looks up two nodes with IDs given in command-line arguments
 * It sets the second node as next to the first one
 * First node then starts an election
 * Used to complete the ring
 * args[0] determines whether to lookup nodes implementing the augmented or original algorithm
 */
public class ManualStartElection {
    public static void main(String args[]) {
        try {
            String regHost = "localhost";
            Integer regPort = 1099;

            Registry reg = LocateRegistry.getRegistry(regHost, regPort);

            Object o1 = reg.lookup("Node" + args[0] + args[1]);
            Node n = (Node) o1;
            Object o = reg.lookup("Node" + args[0] + args[2]);
            Node next = (Node) o;
            n.setNext(next);

            n.startElection();
        } catch (NumberFormatException | RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }
}
