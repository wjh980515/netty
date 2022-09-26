package com.wjh;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static com.wjh.ByteBufferUtil.debugAll;
@Slf4j
public class AIOFileChannel {

    public static void main(String[] args) throws IOException {
        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(Paths.get("data.txt"), StandardOpenOption.READ)) {
            //参数1 ByteBuffer 参数2 读取的起始位置 参数3 附件 参数4 回调对象CompletionHandler
            ByteBuffer buffer = ByteBuffer.allocate(16);
            log.debug("read begin ---");
            channel.read(buffer, 0, buffer,new CompletionHandler<Integer,ByteBuffer>() {
                @Override//read成功回调
                public void completed(Integer result, ByteBuffer attachment) {
                    log.debug("read completed ---");
                    attachment.flip();
                    debugAll(attachment);
                }

                @Override//read异常回调
                public void failed(Throwable exc, ByteBuffer attachment) {
                    exc.printStackTrace();
                }
            });
            log.debug("read finish ---");
        } catch (IOException e) {
        }
        System.in.read();
    }

}
