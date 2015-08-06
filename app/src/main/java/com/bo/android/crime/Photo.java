package com.bo.android.crime;

public class Photo {

    private String filename;

    public Photo() {
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "filename='" + filename + '\'' +
                '}';
    }
}