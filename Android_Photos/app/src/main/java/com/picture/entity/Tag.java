package com.picture.entity;

import java.io.Serializable;

/**
 * Label pair information
 */
public class Tag implements Serializable {
    /**
     * tag number
     */
    private String tagNo;
    /**
     * tag name
     */
    private String tagName;
    /**
     * file name
     */
    private String fileName;
    /**
     * file path
     */
    private String filePath;
    /**
     * Filming locations
     */
    private String location;

    public String getTagNo() {
        return tagNo;
    }

    public void setTagNo(String tagNo) {
        this.tagNo = tagNo;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
