
package com.cts.feedviewer.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.cts.feedviewer.R;
import com.cts.feedviewer.model.FeedCollection;
import com.cts.feedviewer.parser.JsonParser;
import com.cts.feedviewer.parser.JsonParserListener;

/**
 * UI controller for the application.It shows a ListView, handles user inputs and delegates it to respective view.
 * Also acts as listener for parser events as well as for scroll events
 * @author 330016
 */
public class FeedViewerActivity extends Activity implements JsonParserListener, OnScrollListener {

    // View to show the feeds
    private ListView feedList;

    // Collection of model Feed classes, which we will pass to FeedAdapter
    private FeedCollection feedCollection;

    // Adapter to show each of the row items of ListView
    private FeedAdapter feedAdapter;

    // Custom class, which handles all Json parsing
    private JsonParser jsonParser;

    //boolean to check whether list view is currently scrolling?
    private boolean listViewBusy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_viewer);
        // we are passing the context as well as listener(Activity itself)
        // Json parsing will happen in a parallel thread and as soon as parsing done,we will be notified of the event.
        jsonParser = new JsonParser(this, this);
        jsonParser.parseData();
        feedCollection = jsonParser.getFeedCollection();
        feedList = (ListView)findViewById(R.id.feedlist);
        // Pass feedCollection to adapter; right now it is empty.
        feedAdapter = new FeedAdapter(feedCollection, this);
        feedList.setAdapter(feedAdapter);
    }

    /**
     * Parsing is done now. Adapter need to notified, so that it can refer data
     * set from latest collection.
     */
    @Override
    public void onParsingCompleteNotify() {
        feedCollection = jsonParser.getFeedCollection();
        setTitle(feedCollection.getTitle());
        feedAdapter.setItems(feedCollection.getRows());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //We are killing the process here on Activity exit, because user may not come back to the app immediately
        //Since we are holding the Bitmaps in the cache, it might impact other running apps if left as it is.
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {
        // TODO Auto-generated method stub

    }

    /**
     * Here, we will check that whether scrolling is currently happening or not.
     * if scrolling is not happening, then only we will tell ImageDownLoader to
     * download an image.
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                listViewBusy = false;
                feedAdapter.notifyDataSetChanged();
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                listViewBusy = true;
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                listViewBusy = true;
                break;
        }
    }

    public boolean isListViewBusy() {
        return listViewBusy;
    }

}
