package Model;

import java.util.ArrayList;

/**
 * This ConcurrentSim class makes it possible to implement concurrent operations on the Game of Life application
 * using <code>threads</code>.
 * @author Abelsen, Tommy
 * @author Petrovic, Branislav
 */
public class ConcurrentSim {

    private ArrayList<Thread> threadList;

    /**
     * Constructs a new ConcurrentSim object and instantiates the threadList ArrayList.
     */
    public ConcurrentSim(){
        threadList = new ArrayList<>();
    }

    /**
     * Adds a new thread with a <code>Runnable</code> task to the threadList ArrayList.
     * @param task - The task the thread is going to run when started.
     * @see Runnable
     */
    public void addThreadTask(Runnable task) {
        threadList.add(new Thread(task));
    }

    /**
     * Iterates through the threadList ArrayList and starts all the threads withing the ArrayList.
     * When all tasks are done and threads finished, the ArrayList is then cleared.
     */
    public void doWork() {
        for (Thread t : threadList) {
            t.start();
        }
        for (Thread t : threadList) {
           try {
               t.join();
           }
           catch (InterruptedException intEx){
               System.out.println(intEx.getMessage());
           }
        }
        threadList.clear();
    }
}
