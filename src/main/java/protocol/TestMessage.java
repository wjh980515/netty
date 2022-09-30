package protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import message.LoginRequestMessage;

@Slf4j
public class TestMessage {

    public static void main(String[] args) throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(new LoggingHandler()
                ,new MessageCodec()
                ,new LengthFieldBasedFrameDecoder(1024,12,4,0,0));
        //encode
        LoginRequestMessage message = new LoginRequestMessage("zhangsan", "123");
        channel.writeOutbound(message);
        //decode
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null,message,buf);
        channel.writeInbound(buf);
    }

}
