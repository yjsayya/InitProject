package init.project.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);               // 기본 쓰레드 수
        executor.setMaxPoolSize(10);               // 최대 쓰레드 수
        executor.setQueueCapacity(100);            // 큐에 대기 가능한 작업 수
        executor.setThreadNamePrefix("async-");    // 쓰레드 이름 접두사
        executor.setWaitForTasksToCompleteOnShutdown(true); // 앱 종료 시 남은 작업 기다림
        executor.initialize();
        return executor;
    }

    // Java 21 Virtual Thread 기반 Executor
    @Bean(name = "virtualExecutor")
    public Executor virtualExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

}