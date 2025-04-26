package com.myolin.ctabustracker.Model;

public class Alert {

    private final String id;
    private final String headline;
    private final String message;

    public Alert(String id, String headline, String message) {
        this.id = id;
        this.headline = headline;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public String getHeadline() {
        return headline;
    }

    public String getMessage() {
        return message;
    }

}
