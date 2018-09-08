import node.AugmentedAlgorithm;
import node.Node;
import node.OriginalAlgorithm;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Used to automate the process of creating and connecting nodes in a ring.
 */
public class TestAlgorithm {
    private static String host = "localhost";
    private static Integer port = 1099;
    private static int numberOfNodes;
    private static String algorithmType;
    private static boolean randomise;

    /**
     * args[0] specifies the number of nodes to be created.
     * args[1] is -o or -a - original or augmented algorithm.
     * args[2] is -r - specifies a random order of nodes around the ring. (Linear if blank)
     *
     * @param args Used too specify what algorithm to use, how many nodes and what 'mode' to run in.
     */
    public static void main(String[] args) {
        numberOfNodes = Integer.parseInt(args[0]);
        algorithmType = args[1];
        randomise = args.length == 3 && args[2].equals("-r");
        AtomicInteger messageCount = new AtomicInteger();

        try {
            java.rmi.registry.LocateRegistry.createRegistry(port);
            System.out.println("RMI registry ready.");
        } catch (Exception e) {
            System.out.println("Exception starting RMI registry:");
            e.printStackTrace();
        }

        Registry registry = null;
        try {
            registry = LocateRegistry.getRegistry(host, port);
        } catch (RemoteException e) {
            e.printStackTrace();
            System.exit(1);
        }

        List<Integer> ids = new ArrayList<>(numberOfNodes);
        for (int i = 1; i <= numberOfNodes; i++) {
            ids.add(i);
        }
        if (randomise) {
            Collections.shuffle(ids);
        }

        for (int i = (numberOfNodes - 1); i >= 0; i--) {
            Node node;
            try {
                if (algorithmType.equals("-a")) {
                    node = new AugmentedAlgorithm(ids.get(i), messageCount);
                } else {
                    node = new OriginalAlgorithm(ids.get(i), messageCount);
                }
                System.out.println("Node" + algorithmType + ids.get(i));
                registry.rebind("Node" + algorithmType + ids.get(i), node);
                if (i != (numberOfNodes - 1)) {
                    System.out.println("Setting next node to " + ids.get(i + 1));
                    node.setNext((Node) registry.lookup("Node" + algorithmType + ids.get(i + 1)));
                }
                if (i == 0) {
                    Node lastNode = (Node) registry.lookup("Node" + algorithmType + ids.get(numberOfNodes - 1));
                    System.out.println("Setting the 'last' (" + ids.get(numberOfNodes - 1) + ") node's next to " + ids.get((i)));
                    lastNode.setNext(node);
                }
            } catch (NotBoundException | RemoteException e) {
                e.printStackTrace();
            }
        }

        try {
            initialiseOutputFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will initialise the result file(s) with extra information. This includes information about what
     * algorithm is running, how many nodes there are and if the order of nodes is random.
     *
     * @throws IOException If initialisation of the file throws an error
     */
    private static void initialiseOutputFile() throws IOException {
        List<String> lines = new ArrayList<>();
        Path file = Paths.get("UnknownAlgorithm");

        switch (algorithmType) {
            case "-a":
                lines.add("Augmented Algorithm");
                file = Paths.get("AugmentedAlgorithm");
                break;
            case "-o":
                lines.add("Original Algorithm");
                file = Paths.get("OriginalAlgorithm");
                break;
            default:
                lines.add("Unknown Algorithm");
                break;
        }
        lines.add("Number of nodes: " + numberOfNodes);
        lines.add("Randomise order of nodes: " + randomise);
        Files.write(file, lines, Charset.forName("UTF-8"));
    }
}
