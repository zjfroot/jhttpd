package jifeng.jhttpd.poc;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.*;

/**
 * Created by IntelliJ IDEA.
 * Date: 3/16/11
 * Time: 3:34 PM
 *
 * @author Jifeng Zhang
 */
public class NullerServerHandler extends BaseServerHandler {
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        //do nothing, nulling the incomming message.
        ChannelBuffer buf = (ChannelBuffer) e.getMessage();
        System.out.println("Nuller server received message from: " + e.getRemoteAddress());

        StringBuilder sb = new StringBuilder();
        while (buf.readable()) {
            char ch = (char) buf.readByte();
            sb.append(ch);
        }


        System.out.println(sb.toString());
        System.out.flush();
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        e.getCause().printStackTrace();
        Channel ch = e.getChannel();
        ch.close();
    }
}
