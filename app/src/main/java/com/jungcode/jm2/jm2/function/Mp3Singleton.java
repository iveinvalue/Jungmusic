package com.jungcode.jm2.jm2.function;

import android.util.Log;

import com.beaglebuddy.mp3.MP3;

import java.io.IOException;

public class Mp3Singleton {
    private static Mp3Singleton instance = null;

    private MP3 mp3;
    private String title;
    private String lyric;
    private String mp3Path;

    private Mp3Singleton() {
    }

    public static synchronized Mp3Singleton getInstance() {
        if (instance == null) {
            instance = new Mp3Singleton();
        }
        return instance;
    }

    public MP3 setMp3(String path) {
        mp3Path = path;
        try {
            mp3 = new MP3(path);
            setTitle(mp3.getTitle());
            setLyric(mp3.getLyrics());
        } catch (IOException e) {
            Log.d("MP3", "Load MP3 Failed");
        }

        return mp3;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    public MP3 getMp3() {
        return mp3;
    }

    public void setMp3(MP3 mp3) {
        this.mp3 = mp3;
    }

    public void reloadMp3() {
        setTitle(mp3.getTitle());
        setLyric(mp3.getLyrics());
    }
}
