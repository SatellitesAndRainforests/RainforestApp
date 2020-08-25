package com.markstart.rainforest.dataStorage;

import com.markstart.rainforest.model.Track;

public class TrackFileNameCreator {

    public static String createFileName ( Track track ) {
        return track.getTrack_id() + ".ser";
    }

}
