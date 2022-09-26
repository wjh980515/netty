package com.wjh.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

@Slf4j
public class HelloServer {

    public static void main(String[] args) {
        //服务器端启动器 负责组装netty组件，启动服务器
        new ServerBootstrap()
                //一个selector和一个thread就是一个eventLoop
                .group(new NioEventLoopGroup())
                //选择服务器的serverSocketChannel的实现
                .channel(NioServerSocketChannel.class)
                //child == worker 决定worker能执行哪些操作
                .childHandler(
                        //和客户端进行数据读写的通道 负责添加别的handler
                        new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        //添加handler
                        nioSocketChannel.pipeline().addLast(new StringDecoder());//将ByteBuf转换为字符串
                        nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter(){//自定义handler
                            @Override //读事件
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf) msg;
                                log.debug(buf.toString(Charset.defaultCharset()));
                            }
                        });
                    }
                })
                .bind(8080);
    }

}
