package com.markstart.rainforest.dataStorage;

import com.markstart.rainforest.model.Track;


public final class TrackFileNameCreator {

    private TrackFileNameCreator(){}

    public static String createFileName ( Track track ) {
        return track.getTrack_id() + ".ser";
    }

}
