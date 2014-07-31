
package com.cts.feedviewer.ui;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cts.feedviewer.R;
import com.cts.feedviewer.handler.HttpConnectionHandler;
import com.cts.feedviewer.handler.HttpRequest;
import com.cts.feedviewer.handler.HttpResponseListener;
import com.cts.feedviewer.model.Feed;
import com.cts.feedviewer.model.FeedCollection;
import com.cts.feedviewer.model.ImageCache;

/**
 * This is the Adapter class, which takes List<Feed> as input and creates view
 * for each row.
 * 
 * @author 330016
 */
public class FeedAdapter extends BaseAdapter implements HttpResponseListener {
    private List<Feed> feedCollection;

    private static Context context;

    private static HttpConnectionHandler httpHandler;

    public FeedAdapter(FeedCollection feedCollection, Context context) {
        this.feedCollection = feedCollection.getRows();
        this.context = context;
        httpHandler = HttpConnectionHandler.getHttpConnectionHandler(this);
    }

    /**
     * As soon as parsing done, List<Feed> has been passed to this adapter as
     * data set. Here we need to call notifyDataSetChanged() to tell adapter
     * that it need to refresh UI for the changed dataset.
     * 
     * @param feedCollection
     */
    public void setItems(List<Feed> feedCollection) {
        this.feedCollection = feedCollection;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return feedCollection.size();
    }

    @Override
    public Object getItem(int position) {
        return feedCollection.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Feed currentFeed = feedCollection.get(position);
        // We will use holder pattern here. Holder will have a reference to all
        // the views for a row layout.
        // We will set this holder to the root container of each row. If view
        // has no holder attached, then a new holder will be created.
        FeedViewHolder holder;
        if (convertView == null) {
            view = parent.inflate(parent.getContext(), R.layout.feed_listitem, null);
            holder = new FeedViewHolder();
            holder.feedImage = (ImageView)view.findViewById(R.id.feedimage);
            holder.feedTitle = (TextView)view.findViewById(R.id.feedheader);
            holder.feedDesc = (TextView)view.findViewById(R.id.feeddesc);
            view.setTag(holder);
        } else {
            holder = (FeedViewHolder)view.getTag();
        }
        // We will check here whether scrolling is happening or stopped. If
        // stopped, then we tell ImageDownloader to download the images.
        holder.populate(currentFeed, ((FeedViewerActivity)context).isListViewBusy());
        return view;
    }

    static class FeedViewHolder {
        public ImageView feedImage;

        public TextView feedTitle;

        public TextView feedDesc;

        void populate(Feed currentFeed, boolean isBusy) {
            String title = currentFeed.getTitle();
            String description = currentFeed.getDescription();
            String imageUrl = currentFeed.getImageHref();
            // Here, we need to set the View visibility GONE and VISIBLE based
            // on the tag availability
            if (imageUrl == null) {
                feedImage.setVisibility(View.GONE);
            } else {
                feedImage.setVisibility(View.VISIBLE);
            }

            if (title == null) {
                feedTitle.setVisibility(View.GONE);
            } else {
                feedTitle.setVisibility(View.VISIBLE);
            }

            if (description == null) {
                feedDesc.setVisibility(View.GONE);
            } else {
                feedDesc.setVisibility(View.VISIBLE);
            }

            feedTitle.setText(title);
            feedDesc.setText(description);
            if (!isBusy) {
                // We will check first that ImageCache has the Bitmap or not. If
                // yes, we will set to View right away.
                // If not, create a HttpRequest and pass it to
                // HttpConnectionHandler
                if (imageUrl != null) {
                    Bitmap bitmap = ImageCache.getImageCache().getBitmapFromMemCache(imageUrl);
                    if (bitmap == null) {
                        Bitmap progressBitmap = BitmapFactory.decodeResource(
                                context.getResources(), R.drawable.downloading);
                        feedImage.setImageBitmap(progressBitmap);
                        HttpRequest request = new HttpRequest();
                        request.setUrl(imageUrl);
                        httpHandler.addRequest(request);
                    } else {
                        feedImage.setImageBitmap(bitmap);
                    }
                }
            }
        }

    }

    /**
     * HttpConnectionHandler has downloaded some data and set it to ImageCache.
     * we will call notifyDataSetChanged() to tell adapter on this new change,
     * so that it can refresh the UI.
     */
    @Override
    public void recieveResponseFromServer(HttpRequest response) {
    	notifyDataSetChanged();
    }

}
