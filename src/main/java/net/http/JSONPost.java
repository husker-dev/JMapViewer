package net.http;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class JSONPost extends Post{

    private final String json;

    public JSONPost(CharSequence url, String json) {
        super(url);
        this.json = json;
    }


    protected HttpPost createPost(){
        HttpPost post = super.createPost();
        post.addHeader("content-type", "application/json; utf-8");

        try {
            post.setEntity(new StringEntity(json));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return post;
    }
}
