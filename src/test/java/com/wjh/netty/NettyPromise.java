package com.wjh.netty;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

@Slf4j
public class NettyPromise {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        //准备evetloop对象
        EventLoop eventLoop = new NioEventLoopGroup().next();
        //主动创建promise
        DefaultPromise<Integer> promise = new DefaultPromise<>(eventLoop);
        new Thread(()->{
            log.debug("开始计算");
            try {
                int i = 1 / 0;
                //任意线程执行计算，计算完毕向promise填充结果
                Thread.sleep(1000);
                promise.setSuccess(80);
            } catch (Exception e) {
                e.printStackTrace();
                promise.setFailure(e);
            }

        }).start();

        log.debug("等待结果");
        log.debug("结果是{}",promise.get());
    }

}
