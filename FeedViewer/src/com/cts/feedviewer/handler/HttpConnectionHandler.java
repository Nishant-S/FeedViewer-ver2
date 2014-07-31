
package com.cts.feedviewer.handler;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.AsyncTask;

import com.cts.feedviewer.R;
import com.cts.feedviewer.app.FeedApplication;
import com.cts.feedviewer.model.ImageCache;
import com.cts.feedviewer.util.BitmapUtility;

/**
 * This class works to provide a singleton instance of HttpConnectionHandler,
 * which handles all requests for network related process. This class maintains
 * a queue of HttpRequest, which consists of url of image to be fetched. These
 * calls are aynchronous in nature; we start a new AsyncTask for each request to
 * offload task from main UI thread. FeedAdapter, who is responsible to display
 * these Bitmaps, registered itself as listener for events. As soon as task is
 * finished(Bitmap downloaded), this class adds the Bitmap to a central LruCache
 * and update FeedAdapter. Finally, task will be removed from the queue to avoid
 * duplicate network hits.
 * 
 * @author 330016
 */
public class HttpConnectionHandler {
    // Listener for this handler
    private HttpResponseListener responseListener;

    private static HttpConnectionHandler httpConnectionHandler;

    private Context context;

    // Collection,which holds all HttpRequest.
    private HashSet<HttpRequest> requestCollection;

    private HttpConnectionHandler(HttpResponseListener responseListener) {
        this.responseListener = responseListener;
        context = FeedApplication.getAppContext();
        requestCollection = new HashSet<HttpRequest>();
    }

    public static HttpConnectionHandler getHttpConnectionHandler(
            HttpResponseListener responseListener) {
        if (httpConnectionHandler == null) {
            httpConnectionHandler = new HttpConnectionHandler(responseListener);
        }

        return httpConnectionHandler;
    }

    /**
     * We will check whether request already exists; if not, then add to collection set and start a new AsyncTask. 
     * @param request : HttpRequest instance, which holds Bitmap url
     */
    public void addRequest(HttpRequest request) {
        if(!requestCollection.contains(request)){
            requestCollection.add(request);
            DownloadImageTask asyncTask = new DownloadImageTask(request);
            asyncTask.execute(request.getUrl());   
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private String urlBitmap;

        private HttpRequest request;

        DownloadImageTask(HttpRequest request) {
            this.request = request;
        }

        protected Bitmap doInBackground(String... urls) {
            urlBitmap = urls[0];
            Bitmap bitMap = null;
            try {
                if (isDeviceReadyForConnection()) {
                    InputStream in = new java.net.URL(urlBitmap).openStream();
                    // We will pass inputstream to our BitmapUtility along with
                    // target width and height(dimension, which we
                    // need to show on device).It returns a Options object set
                    // with the target width and height
                    BitmapFactory.Options options = BitmapUtility.getBitmapOptions(in,
                            BitmapUtility.IMAGE_MAX_WIDTH, BitmapUtility.IAMGE_MAX_HEIGHT);
                    // we need to create InputStream again here
                    in = new java.net.URL(urlBitmap).openStream();
                    // Now, tell BitmapFactory to provide the resized image even
                    // before downloading in memory cache.
                    bitMap = BitmapUtility.getBitmapByOptions(in, options);
                    request.setStatus(HttpRequest.STATUS_SUCCESS);
                } else {
                    request.setStatus(HttpRequest.STATUS_FAIL);
                }
            } catch (IOException ioe) {
                request.setStatus(HttpRequest.STATUS_FAIL);
            } catch (Exception e) {
                request.setStatus(HttpRequest.STATUS_FAIL);
            }
            return bitMap;
        }

        protected void onPostExecute(Bitmap bitMap) {
            if (bitMap != null) {
                // Bitmap received, add it to ImageCache.
                ImageCache.getImageCache().addBitmapToCache(request.getUrl(), bitMap);
            } else {
                // Bitmap didn't received for some reasons, so set a placeholder
                // failure image to ImageCache.
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.failure);
                ImageCache.getImageCache().addBitmapToCache(request.getUrl(), bitmap);
            }
            // Request is handled now, remove it from collection and notify
            // listener.
            requestCollection.remove(request);
            responseListener.recieveResponseFromServer(request);
        }

    }

    public boolean isDeviceReadyForConnection() {
        if (((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo() != null) {
            return true;
        }
        return false;
    }
}
