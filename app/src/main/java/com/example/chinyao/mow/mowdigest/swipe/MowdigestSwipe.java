package com.example.chinyao.mow.mowdigest.swipe;

import com.example.chinyao.mow.mowdigest.model.MowdigestImage;
import com.example.chinyao.mow.mowdigest.model.MowdigestPopularNews;

/**
 * Created by chinyao on 7/29/2016.
 */
public class MowdigestSwipe {

    private String image;
    private String title;
    private String abstractString;
    private String section;
    private String published_date;
    private MowdigestPopularNews news;

    public MowdigestSwipe(String image, String title) {
        this.image = image;
        this.title = title;
    }

    public MowdigestSwipe(MowdigestPopularNews theNews) {
        // TODO: default image
        this.image = "";
        boolean found = false;
        for (MowdigestImage theImage : theNews.getMedia().get(0).getMediaMetadata()) {
            // also change Glide placeholder() and error() in MowdigestSwipeAdapter.java
            // square320
            // mediumThreeByTwo440
            if (theImage.getFormat().equals("mediumThreeByTwo440")) {
                this.image = theImage.getUrl();
                break;
            }
            // in case the above is not there
            if (!found && theImage.getFormat().equals("sfSpan")) {
                this.image = theImage.getUrl();
                found = true;
                // keep searching
            }
            if (!found) {
                this.image = theImage.getUrl();
                // keep searching
            }
        }
        this.title = theNews.getTitle();
        this.abstractString = theNews.getAbstractString();
        this.section = theNews.getSection();
        this.published_date = theNews.getPublished_date();
        this.news = theNews;
    }

    public MowdigestPopularNews getNews() {
        return news;
    }

    public String getImage() {
        return image;
    }

    public String getPublished_date() {
        return published_date;
    }

    public String getSection() {
        return section;
    }

    public String getAbstractString() {
        return abstractString;
    }

    public String getTitle() {
        return title;
    }
}
