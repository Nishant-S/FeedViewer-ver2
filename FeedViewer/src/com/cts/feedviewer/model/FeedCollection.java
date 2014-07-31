
package com.cts.feedviewer.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class acts as collection of rows
 * 
 * @author 330016
 */
public class FeedCollection {
    private String title;

    private List<Feed> rows;

    public FeedCollection() {
        rows = new ArrayList<Feed>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Feed> getRows() {
        return rows;
    }

    public void setRows(List<Feed> rows) {
        this.rows = rows;
    }
}
