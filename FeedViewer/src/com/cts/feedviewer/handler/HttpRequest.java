package com.cts.feedviewer.handler;

/**
 * Model to hold Bitmap url and state of Bitmap download
 * @author 330016
 *
 */
public class HttpRequest {
    private String url;
    private int status ;
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAIL = 2;
    
  
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    @Override
    public int hashCode(){
        return url.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        HttpRequest request = (HttpRequest) obj;
        return (this.url.equals(request.url));
    }


   
}
