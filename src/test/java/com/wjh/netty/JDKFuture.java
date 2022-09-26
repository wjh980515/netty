package com.wjh.netty;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class JDKFuture {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //创建线程池
        ExecutorService service = Executors.newFixedThreadPool(2);
        //提交任务
        Future<Integer> future = service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.debug("执行计算");
                Thread.sleep(1000);
                return 50;
            }
        });
        //主线程通过future来获取结果
        log.debug("等待结果");
        log.debug("结果{}",future.get());


    }

}
