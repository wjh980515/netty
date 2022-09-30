package com.wjh.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;

import java.nio.charset.Charset;

public class TestRedis {

    public static void main(String[] args) {
        final byte[] LINE = {13,10};
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(workerGroup);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new LoggingHandler());
                    socketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            ByteBuf buffer = ctx.alloc().buffer();
                            buffer.writeBytes("*3".getBytes());
                            buffer.writeBytes(LINE);
                            buffer.writeBytes("$3".getBytes());
                            buffer.writeBytes(LINE);
                            buffer.writeBytes("set".getBytes());
                            buffer.writeBytes(LINE);
                            buffer.writeBytes("$4".getBytes());
                            buffer.writeBytes(LINE);
                            buffer.writeBytes("name".getBytes());
                            buffer.writeBytes(LINE);
                            buffer.writeBytes("$8".getBytes());
                            buffer.writeBytes(LINE);
                            buffer.writeBytes("zhangsan".getBytes());
                            buffer.writeBytes(LINE);
                            ctx.writeAndFlush(buffer);
                        }
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            ByteBuf buf = (ByteBuf) msg;
                            System.out.println(buf.toString(Charset.defaultCharset()));
                        }
                    });
                }
            });
            ChannelFuture future = bootstrap.connect("124.71.29.134",6379).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

}
