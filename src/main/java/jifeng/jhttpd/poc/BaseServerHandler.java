package jifeng.jhttpd.poc;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

/**
 * Created by IntelliJ IDEA.
 * Date: 3/16/11
 * Time: 4:03 PM
 *
 * @author Jifeng Zhang
 */
public class BaseServerHandler extends SimpleChannelHandler {
    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        System.out.println("channel closed");
    }

    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        System.out.println("channel opened");
    }
}
