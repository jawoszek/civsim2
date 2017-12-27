package com.kawiory.civsim2.simulator;

import com.kawiory.civsim2.spring.model.SimulationPreview;
import com.kawiory.civsim2.spring.model.SimulationsList;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Kacper
 */

public class SimulationsExecutor {

    private final LinkedBlockingQueue<Runnable> taskQueue;
    private final List<Runnable> running;
    private final ThreadPoolExecutor pool;

    public SimulationsExecutor(LinkedBlockingQueue<Runnable> taskQueue, List<Runnable> running, ThreadPoolExecutor pool) {
        this.taskQueue = taskQueue;
        this.running = running;
        this.pool = pool;
    }

    public boolean addSimulation(Simulation simulation) throws RejectedExecutionException {
        pool.execute(simulation);
        return true;
    }

    public SimulationsList getWaitingSimulations() {
        SimulationsList list = new SimulationsList();

        taskQueue.stream()
                .map(x -> new SimulationPreview(((Simulation) x)))
                .forEach(list::addWaiting);

        return list;
    }

    public int getWaitingCount() {
        return taskQueue.size();
    }

    public boolean deleteSimulation(int simID) {
        boolean deleted = false;
        for (Iterator<Runnable> i = running.iterator(); i.hasNext(); ) {
            Simulation s = (Simulation) i.next();
            if (s.getId() == simID) {
                s.stop();
                deleted = true;
                break;
            }
        }
        return deleted;
    }

    public void clearQueue() {
        taskQueue.clear();
    }

}
