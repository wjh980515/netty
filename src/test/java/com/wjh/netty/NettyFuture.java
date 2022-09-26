package com.wjh.netty;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

@Slf4j
public class NettyFuture {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        EventLoop eventLoop = eventLoopGroup.next();
        Future<Integer> future = eventLoop.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.debug("执行计算");
                Thread.sleep(1000);
                return 70;
            }
        });
        //主线程通过future来获取结果
//        log.debug("等待结果");
//        log.debug("结果{}",future.get());
        //异步方式接受结果
        future.addListener(new GenericFutureListener<Future<? super Integer>>(){
            @Override
            public void operationComplete(Future<? super Integer> future) throws Exception {
                log.debug("接受结果{}",future.getNow());
            }
        });
    }

}
