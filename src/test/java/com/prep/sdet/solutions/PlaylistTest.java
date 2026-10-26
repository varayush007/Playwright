package com.prep.sdet.solutions;

import static org.testng.Assert.*;

import com.prep.sdet.Playlist;
import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.Test;

public class PlaylistTest {

    @Test
    public void getTracks_returnsInitialTracksInOrder() {
        assertEquals(new Playlist(List.of("a", "b")).getTracks(), List.of("a", "b"));
    }

    @Test // FAILS: bug. Constructor stores the caller's list with no defensive copy
    public void changingSourceListAfterConstruction_doesNotAffectPlaylist() {
        List<String> source = new ArrayList<>(List.of("a", "b"));
        Playlist p = new Playlist(source);
        source.add("c");
        assertEquals(p.getTracks(), List.of("a", "b"));
    }

    @Test // FAILS: bug. getTracks() exposes the internal list
    public void modifyingReturnedList_doesNotAffectPlaylist() {
        Playlist p = new Playlist(new ArrayList<>(List.of("a", "b")));
        try {
            p.getTracks().add("hacked");        // either an unmodifiable list or a copy is acceptable
        } catch (UnsupportedOperationException ignored) {
        }
        assertEquals(p.getTracks(), List.of("a", "b"));
    }

    @Test
    public void with_returnsNewPlaylist_originalUnchanged() {
        Playlist original = new Playlist(new ArrayList<>(List.of("a")));
        Playlist extended = original.with("b");
        assertEquals(extended.getTracks(), List.of("a", "b"));
        assertEquals(original.getTracks(), List.of("a"));
    }

    @Test
    public void constructor_null_throws() {
        assertThrows(IllegalArgumentException.class, () -> new Playlist(null));
    }
}
