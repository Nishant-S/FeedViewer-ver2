
package com.cts.feedviewer.util;

import java.io.InputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * This utility class will check the size of Bitmap on server even before downloading in memory
 * and can download the resized Bitmap according to our need.
 * @author 330016
 *
 */
public class BitmapUtility {

    //Below width and height are dimensions of ImageView width and height, which are reference width and height
    public static final int IMAGE_MAX_WIDTH = 100;
    public static final int IAMGE_MAX_HEIGHT = 100;
   
    
    public static BitmapFactory.Options getBitmapOptions(InputStream is, int targetWidth,
            int targetHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        //setting inJustDecodeBounds value to true means dont load Bitmap now, just tell me its dimension
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, options);
        int srcWidth = options.outWidth;
        int srcHeight = options.outHeight;
        int inSampleSize = 1;
        if (srcHeight > targetHeight || srcWidth > targetWidth) {
            final int halfHeight = srcHeight / 2;
            final int halfWidth = srcWidth / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > targetHeight
                    && (halfWidth / inSampleSize) > targetWidth) {
                inSampleSize *= 2;
            }
        }

        options.inSampleSize = inSampleSize;
        //Now, we know the source Bitmap dimensions and we told Options about our requirements,
        //so we set inJustDecodeBounds to false;means its time to bring the Bitmap from stream.
        options.inJustDecodeBounds = false;
        return options;
    }

    public static Bitmap getBitmapByOptions(InputStream is, BitmapFactory.Options options) {
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
        return bitmap;
    }

}
