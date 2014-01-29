package com;

import com.twitter.finagle.Service;
import com.twitter.finagle.builder.ClientBuilder;
import com.twitter.finagle.http.Http;
import com.twitter.util.Duration;
import com.twitter.util.FutureEventListener;
import com.twitter.util.Throw;
import com.twitter.util.Try;
import org.jboss.netty.handler.codec.http.*;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Created by lala on 14-1-29.
 */
public class HTTPClient {

    public static void main(String[] args) {
        Service<HttpRequest, HttpResponse> httpClient =
                ClientBuilder.safeBuild(
                        ClientBuilder.get()
                                .codec(Http.get())
                                .hosts(new InetSocketAddress("localhost", 8080))
                                .hostConnectionLimit(1));

        HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/");

        try {
            HttpResponse response1 = httpClient.apply(request).apply();
            System.out.println("Get response1...");
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        try {
            HttpResponse response2 = httpClient.apply(request).apply(
                    new Duration(TimeUnit.SECONDS.toNanos(1)));
            System.out.println("Get response2...");
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        Try<HttpResponse> responseTry = httpClient.apply(request).get(
                new Duration(TimeUnit.SECONDS.toNanos(1)));
        if (responseTry.isReturn()) {
            // Cool, I have a response! Get it and do something
            System.out.println("Get response3...");
            HttpResponse response3 = responseTry.get();
        } else {
            // Throw an exception
            Throwable throwable = ((Throw) responseTry).e();
            System.out.println("Exception thrown by client: " + throwable);
        }

        httpClient.apply(request).addEventListener(new FutureEventListener<HttpResponse>() {
            @Override
            public void onSuccess(HttpResponse response4) {
                // Cool, I have a response, do something with it!
                System.out.println("Get response4... : "+response4);
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println("Exception thrown by client: " + throwable);
            }
        });
    }

}

