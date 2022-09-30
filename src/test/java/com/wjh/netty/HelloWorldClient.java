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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HelloWorldClient {

    static final Logger log = LoggerFactory.getLogger(HelloWorldClient.class);

    public static void main(String[] args) {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(worker);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                        //channel建立成功后触发
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {

                            ByteBuf buf = ctx.alloc().buffer(16);
                            buf.writeBytes(new byte[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20});
                            ctx.writeAndFlush(buf);
                            ctx.channel().close();

                        }
                    });
                }
            });
            ChannelFuture future = bootstrap.connect("127.0.0.1",8888).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("client error",e);
        } finally {
            worker.shutdownGracefully();
        }
    }

}
