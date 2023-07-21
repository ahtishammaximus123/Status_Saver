package com.example.stickers.Models;

import java.io.File;
import java.io.Serializable;

public class Status implements Serializable {

    private File file;
//    private Bitmap thumbnail;
    private String title = "";
    private String path = "";
    private boolean isVideo = false;
    public boolean isSavedStatus = false;

    public Status(File file, String title, String path) {
        this.file = file;
        this.title = title;
        this.path = path;
        String MP4 = ".mp4";
        this.isVideo = file.getName().endsWith(MP4);
    }

    public boolean isSavedStatus() {
        return isSavedStatus;
    }

    public void setSavedStatus(boolean savedStatus) {
        isSavedStatus = savedStatus;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

//    public Bitmap getThumbnail() {
//        return thumbnail;
//    }
//
//    public void setThumbnail(Bitmap thumbnail) {
//        this.thumbnail = thumbnail;
//    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }


    @Override
    public String toString() {
        return "Status{" +
                "file=" + file +
                ", title='" + title + '\'' +
                ", path='" + path + '\'' +
                ", isVideo=" + isVideo +
                ", isSavedStatus=" + isSavedStatus +
                '}';
    }
}
