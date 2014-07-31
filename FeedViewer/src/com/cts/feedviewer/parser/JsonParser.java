
package com.cts.feedviewer.parser;

import java.io.IOException;
import java.io.InputStream;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.cts.feedviewer.model.FeedCollection;

import com.google.gson.Gson;

/**
 * Utilty class to parse the Json strings in an Async task, create Feeds and
 * store them to FeedCollection. As soon as data will be
 * parsed,JsonParserListener will be notified.
 * This class uses google Gson library to parse Gson.
 * @author 330016
 */
public class JsonParser {

    private JsonParserListener listener;

    private FeedCollection feedCollection;

    private Context context;

    public static final String jsonPath = "facts.json";

    private static final String TAG = JsonParser.class.getSimpleName();

    public JsonParser(Context context, JsonParserListener listener) {
        this.context = context;
        this.listener = listener;
        feedCollection = new FeedCollection();
    }

    public void parseData() {
        //we dont want to block our main thread, hence start parsing in AsyncTask
        new AsyncParser().execute();
    }

    
    class AsyncParser extends AsyncTask<String, String, String> {
        
        @Override
        protected String doInBackground(String... arg0) {
            try {
                parseJson(loadJSONFromAsset());
            } catch (Exception e) {
                Log.i(TAG, "Exception during parsing " + e.toString());
            }
            return null;
        }

        /**
         * Done with the parsing, so I will notify my listener on this.
         */
        protected void onPostExecute(String file_url) {
            listener.onParsingCompleteNotify();
        }

        /**
         * Below method refers file from asset folder and converts as String
         * 
         * @return Json string
         */
        public String loadJSONFromAsset() {
            String json = null;
            try {
                InputStream is = context.getAssets().open(jsonPath);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer, "UTF-8");
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
            return json;

        }

        public void parseJson(String jsonStr) {
            try {
                Gson gson = new Gson();
                //we pass the json string and the bean class(synonym of the root of json structure)
                feedCollection = gson.fromJson(jsonStr, FeedCollection.class);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    public FeedCollection getFeedCollection() {
        return feedCollection;
    }

}
