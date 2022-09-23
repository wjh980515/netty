package com.wjh;

import java.nio.ByteBuffer;

import static com.wjh.ByteBufferUtil.debugAll;

public class Test4 {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(32);
        buffer.put("Helo,world\nI'm zhangsan\nHo".getBytes());
        split(buffer);
        buffer.put("w are you?\n".getBytes());
        split(buffer);
    }

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

}
