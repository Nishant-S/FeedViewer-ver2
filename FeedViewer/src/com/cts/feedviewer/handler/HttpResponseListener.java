package com.cts.feedviewer.handler;

/**
 * Listener to be registered to listen HttpConnectionHandler events
 * @author 330016
 *
 */
public interface HttpResponseListener {
    public void recieveResponseFromServer(HttpRequest response);
}
