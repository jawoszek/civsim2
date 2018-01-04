package com.kawiory.civsim2.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GeneratorsExecutor {

    private LinkedBlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>(10);
    private List<Runnable> running = Collections.synchronizedList(new ArrayList<>());

    private ThreadPoolExecutor pool = new ThreadPoolExecutor(2, 5, 1000L, TimeUnit.MILLISECONDS, taskQueue){

        @Override
        protected void beforeExecute(Thread t, Runnable r) {
            super.beforeExecute(t, r);
            running.add(r);
        }

        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            super.afterExecute(r, t);
            running.remove(r);
        }
    };

    public boolean addWorld(Generation generation) throws RejectedExecutionException {
        pool.execute(generation);
        return true;
    }

}
