# DAS-Coursework
## An Augmented Version of the Chang &amp; Roberts Algorithm
Coursework for the Distributed Algorithms &amp; Systems course at the University of Glasgow. Course taken in the 2017-2018 academic year. This coursework was completed as part of a team, please see the [Research Article](Research-Article.pdf) for details.

Leader election algorithms are crucial mechanisms which distributed systems use to achieve coordination and agreement. 
Several mutual exclusion algorithms need to elect a leader and use a leader election algorithm to accomplish this. 
One such algorithm is the Chang and Roberts algorithm. 
In this paper, an augmented version of the Chang and Roberts algorithm is proposed that achieves significantly better performance in a single-election worst-case scenario. 
The implementation of the two versions is then discussed, using Java RMI. 
Evaluation has been carried out that illustrates the predictions of the algorithms’ complexity. 
The augmented algorithm sends 2N messages for a single election in both the worst case and best case scenarios, while the original algorithm has a worst-case complexity of 3N − 1.

A research article was produced as a result of this coursework. 
Please see the [Research Article](Research-Article.pdf).
This article will give some useful insight before exploring the code.

All code is in the [src folder](src).

-----------------------------------------------------------------------------------------

DAS Team Project
How To Run

We have made this project as easy as possible to run. After you have compiled the project
on your system, through "javac *.java" or equivalent, you are ready to start nodes and
begin testing. Please make sure that all java files are compiled. For example, from within the 'src' folder:

    $ javac TestAlgorithm.java
    $ javac election/StartElectionRound.java

Through InitializeRing and ManualStartElection it is possible to control the creation of nodes on a
very granular level. To facilitate the testing of the algorithms against hundreds of
nodes we have created a class called **TestAlgorithm**. We start the RMI programmatically in
this class to further add to the ease of use. This class takes a few command line
arguments then does all the heavy lifting for you:
* arg[0]: Specifies the number of nodes to be created in a 'ring'
* arg[1]: Specifies which algorithm you would like to use, whether that be the
            augmented '-a' or original '-o' algorithm.
* arg[2]: Specifies what the ordering of nodes should be round the ring. This allows
            you to test, best '-b', worst '-w' or random 'r' ordering of nodes.
If no errors have been printed and output has stopped, all the nodes have been created
and are ready.

**StartElectionRound** is used to start an election. This class takes 2 or more arguments:
* arg[0]:  Specifies which algorithm you would like to use, whether that be the
             augmented '-a' or original '-o' algorithm.
* arg[1..] Specifies which nodes should start an election. For example if you wanted
             the nodes with ID's 100 and 300, you would simple provide '100 300' as the
             argument(s).
Within this class there is a call to sleep(), this is used to add a delay between
election round starting. For example you could specify a very short delay to see what
happens when two nodes start an election at nearly the same time. Or you could set this
delay to be larger to see what happens when a node starts an election near the end of an
election round. The test data that will be outputted will be in a file called
'output.txt', this provides metrics that allow for the analysis of performance between
the two algorithms.

For Example, from within the 'src' folder':

    $ java TestAlgorithm 500 -a -r

Would start 500 nodes in a random order, that use the augmented algorithm.

    $ java election.StartElectionRound -a 100 300

Make two nodes which use the augmented algorithm, 100 and 300, start an election.

The **InitializeRing** class allows you to start a single node at a time. This is to be used if 
you want to start each node as a separate process. The class takes 2 or more arguments:
* arg[0]: Specifies which algorithm you would like to use, whether that be the
            augmented '-a' or original '-o' algorithm.
* arg[1]: Specifies the ID of the node to be created
* arg[2]: Specifies the ID of the node which should be next to the node that is being created
    
The **ManualStartElection** class is used to complete a ring made by multiple runs of InitializeRing
and start an election. It takes 3 arguments:
* arg[0]: Specifies which algorithm you would like to use, whether that be the
            augmented '-a' or original '-o' algorithm.
* arg[1]: Specifies the ID of the first node to be looked up and start the election
* arg[2]: Specifies the ID of the node which should be set as the next to the first node
