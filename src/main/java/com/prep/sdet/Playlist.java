package com.prep.sdet;

import java.util.ArrayList;
import java.util.List;

/**
 * Extra: immutability / defensive copies.
 * Callers must not be able to change the playlist through the list they passed in
 * or the list returned by getTracks().
 */
public class Playlist {
    private final List<String> tracks;

    public Playlist(List<String> tracks) {
        if (tracks == null) throw new IllegalArgumentException("null");
        this.tracks = tracks;
    }

    public List<String> getTracks() { return tracks; }

    public Playlist with(String track) {
        List<String> copy = new ArrayList<>(tracks);
        copy.add(track);
        return new Playlist(copy);
    }
}
