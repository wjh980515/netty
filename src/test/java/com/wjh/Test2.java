package com.wjh;

import java.nio.ByteBuffer;

public class Test2 {

    public static void main(String[] args) {
        //采用Java堆内存 读写效率较低 受到垃圾回收的影响
        ByteBuffer buffer1 = ByteBuffer.allocate(16);
        //采用直接内存 读写效率高 少一次数据拷贝 不会受到垃圾回收的影响 采用的是系统内存 分配内存的时候效率低 可能会内存泄露
        ByteBuffer buffer2 = ByteBuffer.allocateDirect(16);
    }

}
