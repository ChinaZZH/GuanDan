package com.lbwan.game.server;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.lbwan.game.handler.HandlerHolder;
import com.lbwan.game.handler.HttpHandler;
import com.lbwan.game.spring.SpringUtils;

/**
 * The Class CarrotServerHandler.
 * @author hli
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<DefaultFullHttpRequest> {

    private final Logger logger = Logger.getLogger(HttpServerHandler.class);

    private HandlerHolder handlerHolder;

    public HttpServerHandler() {
        if (handlerHolder == null) {
            synchronized (HttpServerHandler.class) {
                if (handlerHolder == null) {
                    handlerHolder = (HandlerHolder) SpringUtils.getBeanByName("handlerHolder");
                }
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DefaultFullHttpRequest request) throws Exception {
        if (HttpMethod.GET.equals(request.getMethod())) {
            try {
                QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
                Map<String, List<String>> parameters = queryStringDecoder.parameters();

                List<String> cmdList = parameters.get("cmd");
                if (cmdList == null || cmdList.size() == 0) {
                    ctx.close();
                    return;
                }
                String cmd = cmdList.get(0);
                HttpHandler httpHandler = handlerHolder.getHttpHandler(Integer.parseInt(cmd));
                if (httpHandler == null) {
                    logger.warn("http handler not found,cmd is:" + cmd);
                    ctx.close();
                    return;
                }

                String result = httpHandler.handle(parameters);
                if (StringUtils.isBlank(result)) {
                    ctx.close();
                    return;
                }

                List<String> calbackList = parameters.get("callback");
                if (calbackList != null && calbackList.size() > 0) {
                    result = calbackList.get(0) + "(" + result + ");";
                }

                FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer(result,
                        CharsetUtil.UTF_8));

                response.headers().set(CONTENT_TYPE, "application/json; charset=UTF-8");
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            } catch (Exception e) {
                logger.error("when http server handle:", e);
                ctx.close();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("when http server handle:", cause);
        ctx.close();
    }

}
