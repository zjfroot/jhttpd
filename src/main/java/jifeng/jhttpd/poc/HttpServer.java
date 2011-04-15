package jifeng.jhttpd.poc;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Created by IntelliJ IDEA.
 * Date: 3/16/11
 * Time: 4:40 PM
 *
 * @author Jifeng Zhang
 */
public class HttpServer {
    public static void main(String [] args){
        ServerBootstrap bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),Executors.newCachedThreadPool())
        );

        bootstrap.setPipelineFactory(new HttpServerPipelineFactory());

        bootstrap.bind(new InetSocketAddress(10000));
    }
}
