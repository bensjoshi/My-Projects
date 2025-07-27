package com.example.project;

public class Post {
    private String userId;

    private String username;
    private String content;
    private long timestamp;

    public Post() {} // Required for Firebase
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public String getContent() { return content; }
    public long getTimestamp() { return timestamp; }

    public void setUsername(String username) { this.username = username; }
    public void setContent(String content) { this.content = content; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    private String documentId;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }


}
