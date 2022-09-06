package com.wjh;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static com.wjh.ByteBufferUtil.debugAll;

public class Test3 {

    public static void main(String[] args) {
        //字符串转换为ByteBuffer 仍为写模式
        ByteBuffer buffer1 = ByteBuffer.allocate(16);
        buffer1.put("hello".getBytes());
        debugAll(buffer1);
        //借助Charset 直接切换为读模式
        ByteBuffer buffer2 = StandardCharsets.UTF_8.encode("hello");
        debugAll(buffer2);
        //wrap 将字节数组包装为buffer 切换为读模式
        ByteBuffer buffer3 = ByteBuffer.wrap("hello".getBytes());
        debugAll(buffer3);

        String str = StandardCharsets.UTF_8.decode(buffer2).toString();
        System.out.println(str);
    }

}
