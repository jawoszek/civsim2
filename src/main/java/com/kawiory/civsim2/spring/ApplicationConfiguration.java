package com.kawiory.civsim2.spring;

import com.kawiory.civsim2.persistance.DataProvider;
import com.kawiory.civsim2.persistance.pgsql.PGDataProvider;
import com.kawiory.civsim2.simulator.SimulationExecutor;
import org.postgresql.ds.PGPoolingDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Kacper
 */

@Configuration
public class ApplicationConfiguration {


    @Bean
    public SimulationExecutor simulationExecutor() {
        LinkedBlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>(10);
        List<Runnable> running = Collections.synchronizedList(new ArrayList<>());
        ThreadPoolExecutor pool = new ThreadPoolExecutor(2, 5, 1000L, TimeUnit.MILLISECONDS, taskQueue) {

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

        return new SimulationExecutor(taskQueue, running, pool);
    }

    @Bean
    public DataSource dataSource() {
        PGPoolingDataSource source = new PGPoolingDataSource();
        source.setDataSourceName("A Postgresql Data Source");
        source.setServerName("localhost");
        source.setDatabaseName("postgres");
        source.setUser("postgres");
        source.setPassword("password");
        source.setMaxConnections(45);
        return source;
    }

    @Bean
    public DataProvider dataProvider(DataSource dataSource) {
        return new PGDataProvider(dataSource);
    }
}
