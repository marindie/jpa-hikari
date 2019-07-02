package com.mig.base;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableBatchProcessing
@SpringBootApplication
@EnableAsync
public class OracleApplication {

	public static void main(String[] args) {
		SpringApplication.run(OracleApplication.class, args);
	}

    @Bean
    public TaskExecutor taskExecutor() {

        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setQueueCapacity(50);
        taskExecutor.setMaxPoolSize(20);

        return taskExecutor;
    }	
	
}
