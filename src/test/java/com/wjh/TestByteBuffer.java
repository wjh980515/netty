package com.wjh;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@Slf4j
public class TestByteBuffer {

    public static void main(String[] args) {
        //filechannel
        try (FileChannel channel = new FileInputStream("src/data.txt").getChannel()) {
            //准备缓冲区 划分一块内存作为缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(10);
            while (true) {
                //从channel读取数据 向buffer写
                int i = channel.read(buffer);
                log.debug("读取到的字节数{}",i);
                if (i == -1){ // i=-1表示已经没有字节可读
                    break;
                }
                buffer.flip(); //切换buffer为读模式
                while (buffer.hasRemaining()){//buffer有数据就读
                    byte b = buffer.get();
                    log.debug("实际的字节{}",(char) b);
                }
                //buffer切换为写模式
                buffer.clear();
            }
        } catch (IOException e) {
        }
    }

}
