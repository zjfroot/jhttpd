package jifeng.jhttpd.poc;

import static org.jboss.netty.handler.codec.http.HttpHeaders.*;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.*;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.*;
import static org.jboss.netty.handler.codec.http.HttpVersion.*;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.util.CharsetUtil;

public class HttpRequestHandler extends SimpleChannelUpstreamHandler {

    private HttpRequest request;
    private boolean readingChunks;
    /**
     * Buffer that stores the response content
     */
    private final StringBuilder buf = new StringBuilder();

    //private String htdocs = "/home/jifzh/jifeng/pj/jhttpd/wwwhome";

    private String htdocs = "/var/www";


    private String contentType;

    File requestedFile;

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (!readingChunks) {
            HttpRequest request = this.request = (HttpRequest) e.getMessage();

            if (is100ContinueExpected(request)) {
                send100Continue(e);
            }

            //clear the buf

            buf.setLength(0);

            Logger.log("jhttpd web server");
            Logger.log("VERSION: " + request.getProtocolVersion());
            Logger.log("HOSTNAME: " + getHost(request, "unknown"));
            Logger.log("REQUEST_URI: " + request.getUri());
            

            String requestedFileName = request.getUri();

            /*if(requestedFileName.equals("/test")){
                Thread.sleep(10000);
            }*/

            contentType = guessContentType(requestedFileName);

            HttpResponseStatus statusCode;

            requestedFile = new File(htdocs + requestedFileName);

            if(!requestedFile.exists()){
                statusCode = NOT_FOUND;
            }else{
                if(requestedFile.isDirectory()){

                }else{
                    
                }
                statusCode = OK;
            }



            for (Map.Entry<String, String> h : request.getHeaders()) {
                Logger.log("HEADER: " + h.getKey() + " = " + h.getValue());
                
            }


            if (request.isChunked()) {
                readingChunks = true;
            } else {
                ChannelBuffer content = request.getContent();
                if (content.readable()) {
                    Logger.log("CONTENT: " + content.toString(CharsetUtil.UTF_8));
                    
                }
                writeResponse(e,statusCode);
            }
        } else {
            HttpChunk chunk = (HttpChunk) e.getMessage();
            if (chunk.isLast()) {
                readingChunks = false;
                /*buf.append("END OF CONTENT\r\n");*/

                HttpChunkTrailer trailer = (HttpChunkTrailer) chunk;
                /*if (!trailer.getHeaderNames().isEmpty()) {
                    buf.append("\r\n");
                    for (String name : trailer.getHeaderNames()) {
                        for (String value : trailer.getHeaders(name)) {
                            buf.append("TRAILING HEADER: " + name + " = " + value + "\r\n");
                        }
                    }
                    buf.append("\r\n");
                }*/

                writeResponse(e, OK);
            } else {
                /*buf.append("CHUNK: " + chunk.getContent().toString(CharsetUtil.UTF_8) + "\r\n");*/
            }
        }
    }

    private void writeResponse(MessageEvent e, HttpResponseStatus statusCode) {
        // Decide whether to close the connection or not.
        boolean keepAlive = isKeepAlive(request);

        // Build the response object.
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, statusCode);

        if(contentType.equals("text/html") || contentType.equals("text/plain")){


            buf.append(FileUtil.readFile(requestedFile));
        }




        response.setContent(ChannelBuffers.copiedBuffer(buf.toString(), CharsetUtil.UTF_8));
        response.setHeader(CONTENT_TYPE, contentType+"; charset=UTF-8");

        if (keepAlive) {
            // Add 'Content-Length' header only for a keep-alive connection.
            response.setHeader(CONTENT_LENGTH, response.getContent().readableBytes());
        }

        // Encode the cookie.
        String cookieString = request.getHeader(COOKIE);
        if (cookieString != null) {
            CookieDecoder cookieDecoder = new CookieDecoder();
            Set<Cookie> cookies = cookieDecoder.decode(cookieString);
            if (!cookies.isEmpty()) {
                // Reset the cookies if necessary.
                CookieEncoder cookieEncoder = new CookieEncoder(true);
                for (Cookie cookie : cookies) {
                    cookieEncoder.addCookie(cookie);
                }
                response.addHeader(SET_COOKIE, cookieEncoder.encode());
            }
        }

        // Write the response.
        ChannelFuture future = e.getChannel().write(response);

        // Close the non-keep-alive connection after the write operation is done.
        if (!keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void send100Continue(MessageEvent e) {
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, CONTINUE);
        e.getChannel().write(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
            throws Exception {
        e.getCause().printStackTrace();
        e.getChannel().close();
    }

    private static String guessContentType(String path)
    {
        if (path.endsWith(".html") || path.endsWith(".htm"))
            return "text/html";
        else if (path.endsWith(".txt") || path.endsWith(".java"))
            return "text/plain";
        else if (path.endsWith(".gif"))
            return "image/gif";
        else if (path.endsWith(".jpg") || path.endsWith(".jpeg"))
            return "image/jpeg";
        else
            return "unknown";
    }
}