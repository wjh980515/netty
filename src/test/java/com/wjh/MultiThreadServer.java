package com.wjh;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

import static com.wjh.ByteBufferUtil.debugAll;

@Slf4j
public class MultiThreadServer {

    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("boss");
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        Selector boss = Selector.open();
        SelectionKey bossKey = ssc.register(boss, 0, null);
        bossKey.interestOps(SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8080));
        //创建固定数量的worker对象并进行初始化 worker的数量设置为计算机的核心数
        Worker[] workers = new Worker[Runtime.getRuntime().availableProcessors()];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker("worker-" + i);
        }
        AtomicInteger index = new AtomicInteger();
        while (true) {
            boss.select();
            Iterator<SelectionKey> iterator = boss.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()){
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    log.debug("连接建立{}",sc.getRemoteAddress());
                    //关联worker的selector
                    log.debug("before 注册{}",sc.getRemoteAddress());
                    //round robin 轮询算法 1 2 1 2 循环
                    workers[index.getAndIncrement() % workers.length].register(sc);
                    log.debug("after 注册{}",sc.getRemoteAddress());
                }
            }
        }
    }

    static class Worker implements Runnable{

        private String name;
        private Thread thread;
        private Selector selector;

        //在线程之间传递数据使用对列
        private ConcurrentLinkedDeque<Runnable> queue = new ConcurrentLinkedDeque<Runnable>();

        private volatile Boolean start = false;//表示是否初始化

        public Worker(String name) {
            this.name = name;
        }

        //初始化线程和selector
        public void register(SocketChannel sc) throws IOException {
            if (!start){
                selector = Selector.open();
                thread = new Thread(this,name);
                thread.start();
                start = true;
            }
            //向队列里添加任务，但是该任务不会立刻执行
            queue.add(()->{
                try {
                    sc.register(selector,SelectionKey.OP_READ,null);
                } catch (ClosedChannelException e) {
                    throw new RuntimeException(e);
                }
            });
            selector.wakeup();
        }

        @Override
        public void run() {
            while (true) {
                try {
                    selector.select();
                    Runnable task = queue.poll();//取出队列里的任务执行
                    if (task != null) {
                        task.run();//执行取出的任务
                    }
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if (key.isReadable()){
                            ByteBuffer buffer = ByteBuffer.allocate(16);
                            SocketChannel channel = (SocketChannel) key.channel();
                            log.debug("读数据{}",channel.getRemoteAddress());
                            channel.read(buffer);
                            buffer.flip();
                            debugAll(buffer);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
