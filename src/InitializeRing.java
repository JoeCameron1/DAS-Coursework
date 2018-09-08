import node.AugmentedAlgorithm;
import node.Node;
import node.OriginalAlgorithm;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.atomic.AtomicInteger;

/* Creates a new node with ID args[1]
 * If ID of next node is given (args[2]) it sets it as next of newly created node
 * args[0] determine whether augmented or original algorithm is used
 */
public class InitializeRing {
    public static void main(String args[]) {
        try {
            String regHost = "localhost";
            Integer regPort = 1099;
            Node n = null;
            AtomicInteger messageCount = new AtomicInteger();

            if (args[0].equals("-a")) {
                System.out.println("Augmented algorithm");
                n = new AugmentedAlgorithm(Integer.parseInt(args[1]), messageCount);
            } else {
                System.out.println("Original algorithm");
                n = new OriginalAlgorithm(Integer.parseInt(args[1]), messageCount);
            }
            Registry reg = LocateRegistry.getRegistry(regHost, regPort);
            reg.rebind("Node" + args[0] + args[1], n);

            if (args.length > 2) {
                Object o = reg.lookup("Node" + args[0] + args[2]);
                Node next = (Node) o;
                n.setNext(next);
            }
        } catch (NumberFormatException | RemoteException | NotBoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
