package net.http;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class HttpRequest {

    private final String url;

    private BasicCookieStore cookieStore = new BasicCookieStore();
    private CloseableHttpResponse response;

    private UsernamePasswordCredentials auth;

    private String userAgent = "Mozilla/5.0 Firefox/26.0";

    public HttpRequest(CharSequence url){
        this.url = url.toString();
    }

    public abstract void execute() throws IOException;

    public void addCookie(Cookie... cookies){
        for(Cookie cookie : cookies)
            cookieStore.addCookie(cookie.getApacheCookie());
    }

    public Cookie[] getCookies(){
        ArrayList<Cookie> cookies = new ArrayList<>();
        for(org.apache.http.cookie.Cookie cookie : cookieStore.getCookies())
            cookies.add(new Cookie(cookie));
        return cookies.toArray(new Cookie[0]);
    }

    public String getLastHeader(String name){
        return response != null && (response.getLastHeader(name) != null) ? response.getLastHeader(name).getValue() : null;
    }

    public String getFirstHeader(String name){
        return response != null && (response.getFirstHeader(name) != null) ? response.getFirstHeader(name).getValue() : null;
    }

    public String getHtmlContent() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));

        StringBuilder out = new StringBuilder();

        String inputLine;
        while ((inputLine = br.readLine()) != null)
            out.append(inputLine).append("\n");
        br.close();
        return out.toString();
    }

    public InputStream getInputStream() throws IOException {
        return response.getEntity().getContent();
    }

    public String getUrl(){
        return url;
    }

    protected void setResponse(CloseableHttpResponse response){
        this.response = response;
    }

    protected void setCookieStore(BasicCookieStore cookieStore){
        this.cookieStore = cookieStore;
    }

    public BasicCookieStore getCookieStore(){
        return cookieStore;
    }

    protected CloseableHttpClient createClient(){
        return HttpClientBuilder.create()
                .setDefaultCredentialsProvider(new BasicCredentialsProvider(){{
                    if(auth != null)
                        setCredentials(AuthScope.ANY, auth);
                }})
                .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
                .setDefaultCookieStore(getCookieStore())
                .setUserAgent(userAgent)
                .build();
    }

    public void setAuth(BasicAuth auth){
        if(auth != null)
            this.auth = new UsernamePasswordCredentials(auth.getLogin(), auth.getPassword());
    }

    protected void execute(HttpRequestBase request) throws IOException {
        HttpClientContext context = HttpClientContext.create();

        setResponse(createClient().execute(request, context));
        setCookieStore((BasicCookieStore) context.getCookieStore());
    }

    public void setUserAgent(String userAgent){
        this.userAgent = userAgent;
    }

    public static class BasicAuth{

        private final String login, password;

        public BasicAuth(String login, String password){
            this.login = login;
            this.password = password;
        }

        public BasicAuth(String login){
            this(login, null);
        }

        public String getLogin() {
            return login;
        }

        public String getPassword() {
            return password;
        }

    }

}
