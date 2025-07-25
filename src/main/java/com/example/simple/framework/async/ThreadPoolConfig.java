package com.example.simple.framework.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步任务线程池配置
 */
@Configuration
public class ThreadPoolConfig {

    /**
     * 定义一个专用于执行异步任务的线程池
     * Bean 的名称 "asyncTaskExecutor" 是 Spring @Async 默认寻找的名称
     */
    @Bean("asyncTaskExecutor")
    public Executor asyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数：CPU核心数的2倍
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors() * 2);
        // 最大线程数
        executor.setMaxPoolSize(20);
        // 任务队列容量
        executor.setQueueCapacity(200);
        // 线程空闲后的最大存活时间
        executor.setKeepAliveSeconds(60);
        // 线程名称前缀
        executor.setThreadNamePrefix("async-task-");
        // 拒绝策略：当队列和线程池都满了，由调用者所在的线程来执行任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 初始化
        executor.initialize();
        return executor;
    }
}