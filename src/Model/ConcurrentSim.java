package Model;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Tommy on 02.05.2017.
 */
public class ConcurrentSim {

    private ArrayList<Thread> threads = new ArrayList<>();

    public void addThreadTask(Runnable task) {
        threads.add(new Thread(task));
    }

    public void doWork() {

        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
           try {
               t.join();
           }
           catch (InterruptedException intEx){
               System.out.println(intEx.getMessage());
           }
        }
        threads.clear();
    }


}
