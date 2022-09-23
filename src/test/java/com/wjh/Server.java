package com.wjh;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import static com.wjh.ByteBufferUtil.debugRead;

@Slf4j
public class Server {

    public static void main(String[] args) throws IOException {
        //使用nio 阻塞模式
        ByteBuffer buffer = ByteBuffer.allocate(16);
        //创建服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();
        //设置为非阻塞模式 影响ssc.accept()
        ssc.configureBlocking(false);
        //绑定监听端口
        ssc.bind(new InetSocketAddress(8001));
        //建立连接集合
        List<SocketChannel> channels = new ArrayList<>();
        while (true) {
            //建立与客户端的连接 SocketChannel sc与客户端通信
            log.debug("connecting---");
            SocketChannel sc = ssc.accept(); //非阻塞 没有连接继续运行 没有链接返回null
            if (sc != null){
                log.debug("connecting--->{}",sc);
                //设置非阻塞模式 影响channel.read(buffer)
                sc.configureBlocking(false);
                channels.add(sc);
            }
            //接收数据
            for (SocketChannel channel : channels) {
                log.debug("before read ===>{}",channel);
                int read = channel.read(buffer);//非阻塞方法 客户端没有发送数据，线程也会继续运行 如果没有读到数据返回0
                if (read != 0) {
                    buffer.flip();
                    debugRead(buffer);
                    buffer.clear();
                    log.debug("after read ===>{}",channel);
                }
            }
        }
    }

}
