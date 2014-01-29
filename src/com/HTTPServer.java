package com;

import com.twitter.finagle.Service;
import com.twitter.finagle.builder.ServerBuilder;
import com.twitter.finagle.http.Http;
import com.twitter.util.Future;
import com.twitter.util.FutureTransformer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.*;

import java.net.InetSocketAddress;

/**
 * Created by lala on 14-1-29.
 */
public class HTTPServer extends Service<HttpRequest, HttpResponse> {

    private Future<String> getContentAsync(HttpRequest request) {
        // asynchronously gets content, possibly by submitting
        // a function to a FuturePool
        return Future.value("content");
    }

    public Future<HttpResponse> apply(HttpRequest request) {

        Future<String> contentFuture = getContentAsync(request);
        return contentFuture.transformedBy(new FutureTransformer<String, HttpResponse>() {
            @Override
            public HttpResponse map(String content) {
                HttpResponse httpResponse =
                        new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                httpResponse.setContent(ChannelBuffers.wrappedBuffer(content.getBytes()));
                return httpResponse;
            }

            @Override
            public HttpResponse handle(Throwable throwable) {
                HttpResponse httpResponse =
                        new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.SERVICE_UNAVAILABLE);
                httpResponse.setContent(ChannelBuffers.wrappedBuffer(throwable.toString().getBytes()));
                return httpResponse;
            }
        });
    }

    public static void main(String[] args) {
        ServerBuilder.safeBuild(new HTTPServer(),
                ServerBuilder.get()
                        .codec(Http.get())
                        .name("HTTPServer")
                        .bindTo(new InetSocketAddress("localhost", 8080)));
        System.out.println("Server start...");
    }
}