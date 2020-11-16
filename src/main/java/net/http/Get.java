package net.http;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;

import java.io.IOException;
import java.util.Map;

public class Get extends HttpRequest {

    public Get(CharSequence url) {
        super(url);
    }

    public void execute() throws IOException {
        // Post request itself
        HttpGet get = new HttpGet(getUrl());

        execute(get);
    }
}
