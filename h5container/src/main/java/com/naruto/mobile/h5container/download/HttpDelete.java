
package com.naruto.mobile.h5container.download;

import java.net.URI;

import org.apache.http.client.methods.HttpPost;

public class HttpDelete extends HttpPost {

    public HttpDelete() {
        super();
    }

    public HttpDelete(String uri) {
        super(uri);
    }

    public HttpDelete(URI uri) {
        super(uri);
    }

    public String getMethod() {
        return "DELETE";
    }

}
