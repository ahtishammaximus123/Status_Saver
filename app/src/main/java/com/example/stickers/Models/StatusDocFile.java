package com.example.stickers.Models;

import androidx.documentfile.provider.DocumentFile;

import java.io.Serializable;

public class StatusDocFile implements Serializable {

    private transient DocumentFile file;
    //    private Bitmap thumbnail;
    private String title;
    private String path;
    private boolean isVideo;
    public boolean isSavedStatus;

    public StatusDocFile(DocumentFile file, String title, String path) {
        this.file = file;
        this.title = title;
        this.path = path;
        String MP4 = ".mp4";
        if (file != null)
            if (file.getName() != null)
                this.isVideo = file.getName().endsWith(MP4);
            else
                this.isVideo = false;
        else
            this.isVideo = false;
    }

    public boolean isSavedStatus() {
        return isSavedStatus;
    }

    public void setSavedStatus(Boolean newStatus) {
        this.isSavedStatus = newStatus;
    }

    public void setSavedStatus(boolean savedStatus) {
        isSavedStatus = savedStatus;
    }

    public DocumentFile getFile() {
        return file;
    }

    public void setFile(DocumentFile file) {
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
}
