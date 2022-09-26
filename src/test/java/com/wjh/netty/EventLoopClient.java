package com.wjh.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

public class EventLoopClient {

    public static void main(String[] args) throws InterruptedException {
        Channel channel = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {

                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        //编码器
                        nioSocketChannel.pipeline().addLast(new StringEncoder());
                    }
                })
                //连接服务器
                .connect(new InetSocketAddress("localhost", 8080))
                .sync() //阻塞方法，直到连接建立才往下进行
                .channel();//连接对象
        System.out.println(channel);
        System.out.println("hello");
    }

}
