package com.android.noteapp;

public class Pdf {
    private String fileName;
    private String fileUrl;
    private String uploader;
    private String phoneNo;
    private String fileId;

    public Pdf() {
        // Empty constructor required for Firebase
    }

    public Pdf(String fileName, String fileUrl, String uploader, String phoneNo, String fileId) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.uploader = uploader;
        this.phoneNo = phoneNo;
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getUploader() {
        return uploader;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getFileId() {
        return fileId;
    }

}
