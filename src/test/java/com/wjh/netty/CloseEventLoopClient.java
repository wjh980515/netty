package com.wjh.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Scanner;

@Slf4j
public class CloseEventLoopClient {

    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture channelFuture = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {

                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        //编码器
                        nioSocketChannel.pipeline().addLast(new StringEncoder());
                        nioSocketChannel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    }
                })
                //连接服务器
                .connect(new InetSocketAddress("localhost", 8080));
        Channel channel = channelFuture.sync().channel();//阻塞方法，直到连接建立才往下进行
        log.debug("{}",channel);
        new Thread(()->{
            Scanner scanner = new Scanner(System.in);
            while (true){
                String line = scanner.nextLine();
                if ("q".equals(line)){
                    channel.close();
                    break;
                }
                channel.writeAndFlush(line);
            }
        },"input").start();

        //获取CloseFuture对象
        //1. 同步处理关闭
        ChannelFuture closeFuture = channel.closeFuture();
        /*System.out.println("waiting close---");
        closeFuture.sync();
        log.debug("关闭之后的操作");*/
        //2. 异步处理关闭
        closeFuture.addListener((ChannelFutureListener) channelFuture1 -> {
            log.debug("关闭之后的操作");
            //优雅的停下来，不是立刻停止，将运行的线程运行完再停止
            group.shutdownGracefully();
        });
    }

}
