package Model;

import java.util.ArrayList;


public class ConcurrentSim {

    private ArrayList<Thread> threadList = new ArrayList<>();

    public void addThreadTask(Runnable task) {
        threadList.add(new Thread(task));
    }

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
