package com.wjh;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;


import static com.wjh.ByteBufferUtil.debugAll;



@Slf4j
public class SelectorServer {

    private static void split(ByteBuffer buffer) {
        buffer.flip();
        for (int i = 0; i < buffer.limit(); i++) {
            if (buffer.get(i) == '\n') {
                int length = i + 1 - buffer.position();
                ByteBuffer target = ByteBuffer.allocate(length);
                for (int j = 0; j < length; j++) {
                    target.put(buffer.get());
                }
                debugAll(target);
            }
        }
        buffer.compact();
    }

    public static void main(String[] args) throws IOException {
        //使用selector
        //创建selector 管理多个channel
        Selector selector = Selector.open();

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        //建立selector与channel之间的联系 注册
        //SelectionKey事件发生后通过它可以知道是事件和哪个channel发生的事件

        SelectionKey sscKey = ssc.register(selector, 0, null);
        log.debug("register key {}",sscKey);
        //sscKey只关注accept事件
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8888));
        while (true) {
            //调用selector的select方法 没有事件，阻塞，有事件线程恢复运行
            selector.select();
            //处理事件 selectedKeys内部包含了所有发生的事件的集合 set
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                //一定要删除key 因为事件处理过之后selectedKeys不会主动删除 需要手动去删除
                iterator.remove();
                log.debug("key {}", key);
                //区分事件类型，不同的事件做不同的操作
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = channel.accept();
                    //在socketChannel上做读取数据的操作
                    socketChannel.configureBlocking(false);
                    ByteBuffer buf = ByteBuffer.allocate(16);
                    //将buffer作为一个selectionKey的附件 attachment
                    SelectionKey scKey = socketChannel.register(selector, 0, buf);
                    //关注读事件
                    scKey.interestOps(SelectionKey.OP_READ);
                    log.debug("socket {}", socketChannel);
                } else if (key.isReadable()) {
                    try {
                        //拿到触发事件的channel
                        SocketChannel sc = (SocketChannel) key.channel();
                        //获取附件 buffer
                        ByteBuffer byteBuffer = (ByteBuffer) key.attachment();
                        int read = sc.read(byteBuffer);
                        //如果是正常断开read的返回值为-1 断开连接的时候会出发一次read事件
                        if (read == -1) {
                            key.cancel();
                        }else {
                            split(byteBuffer);
                            //如果预定义的ByteBuffer不够就new一个它容量二倍的新ByteBuffer并将新的ByteBuffer作为附件添加给selectionKey
                            if (byteBuffer.position() == byteBuffer.limit()){
                                ByteBuffer newByteBuffer = ByteBuffer.allocate(byteBuffer.capacity()*2);
                                byteBuffer.flip();
                                newByteBuffer.put(byteBuffer);
                                key.attach(newByteBuffer);
                            }
                        }
                    }catch (IOException e) {
                        e.printStackTrace();
                        key.cancel();//客户端断开 将key取消 从selector集合中真正删除
                    }
                }


            }
        }
    }

}
