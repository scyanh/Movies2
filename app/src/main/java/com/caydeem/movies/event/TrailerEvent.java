package com.caydeem.movies.event;


import com.caydeem.movies.models.Trailer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kunaljaggi on 4/30/16.
 */
public class TrailerEvent {
    private List<Trailer> mTrailerList = new ArrayList<Trailer>();

    public TrailerEvent(List<Trailer> mTrailerList) {
        this.mTrailerList = mTrailerList;
    }

    public List<Trailer> getmTrailerList() {
        return mTrailerList;
    }

    public void setmTrailerList(List<Trailer> mTrailerList) {
        this.mTrailerList = mTrailerList;
    }
}
