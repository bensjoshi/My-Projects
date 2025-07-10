package com.FutureFridges;

import com.google.firebase.Timestamp;

public class Notification {
    private Timestamp date;
    private String text;

    public Notification() {}

    public Notification(Timestamp date, String text) {
        this.date = date;
        this.text = text;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
