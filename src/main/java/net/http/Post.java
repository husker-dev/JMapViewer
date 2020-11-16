package net.http;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Post extends HttpRequest {

    private final HashMap<String, String> parameters = new HashMap<>();

    public Post(CharSequence url){
        super(url);
    }

    public void setParameter(String name, String value){
        parameters.put(name, value);
    }

    public Map<String, String> getParameters(){
        return parameters;
    }

    public void execute() throws IOException {
        execute(createPost());
    }

    protected HttpPost createPost(){
        HttpPost post = new HttpPost(getUrl()){{
            for(Map.Entry<String, String> entry : getParameters().entrySet())
                setParameter(entry.getKey(), entry.getValue());
        }};
        post.setEntity(new UrlEncodedFormEntity(new ArrayList<>() {{
            for (Map.Entry<String, String> entry : getParameters().entrySet())
                add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }}, StandardCharsets.UTF_8));
        return post;
    }

}
