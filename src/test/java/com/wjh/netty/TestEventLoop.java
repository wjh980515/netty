package com.wjh.netty;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.NettyRuntime;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class TestEventLoop {

    public static void main(String[] args) {
        //创建事件循环组
        EventLoopGroup group = new NioEventLoopGroup(2);//io事件 普通任务 定时任务
        //EventLoopGroup group = new DefaultEventLoopGroup();//普通任务 定时任务 不处理io事件
        System.out.println(NettyRuntime.availableProcessors());
        //获取下一个事件循环对应
        System.out.println(group.next());
        System.out.println(group.next());
        System.out.println(group.next());
        System.out.println(group.next());
        //执行普通任务
        /*group.next().submit(() -> {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            log.debug("ok");
        });
        log.debug("main");*/
        //定时任务
        group.next().scheduleAtFixedRate(() -> {
            log.debug("ok");
        },0,1, TimeUnit.SECONDS);
        log.debug("main");
    }

}
