package net.http;

import org.apache.http.impl.client.BasicCookieStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Http {

    public static boolean debug = false;

    private BasicCookieStore cookieStore = new BasicCookieStore();
    private HttpRequest request;
    private String lastContent = "";

    public Http get(CharSequence url) throws IOException {
        request = new Get(url);

        execute();
        return this;
    }

    public Http postJSON(CharSequence url, String json) throws IOException {
        request = new JSONPost(url, json);

        execute();
        return this;
    }

    public Http post(CharSequence url) throws IOException {
        return post(url, null, null);
    }

    public Http post(CharSequence url, HttpRequest.BasicAuth auth) throws IOException {
        return post(url, null, auth);
    }

    public Http post(CharSequence url, HashMap<String, String> parameters) throws IOException {
        return post(url, parameters, null);
    }

    public Http post(CharSequence url, HashMap<String, String> parameters, HttpRequest.BasicAuth auth) throws IOException {
        request = new Post(url);

        if(parameters != null) {
            for(Map.Entry<String, String> entry : parameters.entrySet())
                ((Post)request).setParameter(entry.getKey(), entry.getValue());
        }
        if(auth != null)
            request.setAuth(auth);

        execute();
        return this;
    }

    public Http execute(HttpRequest request) throws IOException {
        this.request = request;
        execute();
        return this;
    }

    protected void execute() throws IOException {
        request.setCookieStore(cookieStore);
        request.execute();
        cookieStore = request.getCookieStore();

        if(debug)
            printInfo();
    }

    public String getLastHeader(String name){
        return request == null ? null : request.getLastHeader(name);
    }

    public String getFirstHeader(String name){
        return request == null ? null : request.getFirstHeader(name);
    }

    public String getUrl(){
        return request.getUrl();
    }

    public Cookie[] getCookies(){
        ArrayList<Cookie> cookies = new ArrayList<>();
        for(org.apache.http.cookie.Cookie cookie : cookieStore.getCookies())
            cookies.add(new Cookie(cookie));
        return cookies.toArray(new Cookie[0]);
    }

    public void addCookie(Cookie... cookies){
        for(Cookie cookie : cookies)
            cookieStore.addCookie(cookie.getApacheCookie());
    }

    public String getHtmlContent(){
        try{
            lastContent = request == null ? null : request.getHtmlContent();
        }catch (Exception ex){
        }
        return lastContent;
    }

    public BasicCookieStore getCookieStore(){
        return cookieStore;
    }

    public void setCookieStore(BasicCookieStore cookieStore){
        this.cookieStore = cookieStore;
    }

    public void clearCookies(){
        cookieStore.clear();
    }

    public void toLocation() throws IOException {
        get(getFirstHeader("location"));
    }

    public void printInfo(){
        System.out.println("-------------------------------");
        System.out.println((request instanceof Get ? "GET " : "POST ") + request.getUrl());
        for(Cookie cookie : getCookies())
            System.out.println("Cookie: " + cookie.getName() + " = " + cookie.getValue());
        System.out.println("Location: " + request.getLastHeader("location"));
        System.out.println("Content: " + getHtmlContent());
    }
}
