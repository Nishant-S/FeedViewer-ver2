
package com.cts.feedviewer.model;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

/**
 * This provides cache as a central data entity to hold Bitmaps in process memory. The cache size is based on the current available memory,hence
 * we can sure that it will never run out of memory.  
 * @author 330016
 *
 */
public class ImageCache {
    //We are considering here LruCache due to the fact that it will keep elements in the memory till it is not
    //forced to add a new entry beyond its limit. In that case, it will gracefully remove last accessed item to make way 
    //for new item.
    private LruCache<String, Bitmap> mMemoryCache;

    private static ImageCache imageCache;

    private ImageCache() {
        final int maxMemory = (int)(Runtime.getRuntime().maxMemory() / 1024);
        //We are reserving 1/8th of the available runtime memory for our cache.
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // return cache size in kb
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public static ImageCache getImageCache() {
        if (imageCache == null) {
            imageCache = new ImageCache();
        }
        return imageCache;
    }

    public void addBitmapToCache(String url, Bitmap bitmap) {
        if (mMemoryCache.get(url) == null) {
            mMemoryCache.put(url, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String url) {
        return mMemoryCache.get(url);
    }

}
