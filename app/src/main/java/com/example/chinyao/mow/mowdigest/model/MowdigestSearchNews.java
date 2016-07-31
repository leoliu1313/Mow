package com.example.chinyao.mow.mowdigest.model;

import java.util.List;

/**
 * Created by chinyao on 7/31/2016.
 */
public class MowdigestSearchNews {
    String web_url;
    MowdigestHeadline headline; // title
    String section_name;
    String pub_date;
    String snippet; // abstract
    List<MowdigestImage> multimedia;

    public List<MowdigestImage> getMultimedia() {
        return multimedia;
    }

    public String getPub_date() {
        return pub_date;
    }

    public String getSection_name() {
        return section_name;
    }

    public String getSnippet() {
        return snippet;
    }

    public MowdigestHeadline getHeadline() {
        return headline;
    }

    public String getWeb_url() {
        return web_url;
    }
}
