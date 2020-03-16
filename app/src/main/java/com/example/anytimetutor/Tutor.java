package com.example.anytimetutor;

public class Tutor {

    private String sap_id, name, subject;
    private String shortdesc;
    private double rating, credit_score;
    private int sess_count;

    public Tutor(String id, String name, String subject, String shortdesc, double rating, double credit, int sess_count) {
        this.sap_id = id;
        this.name = name;
        this.subject = subject;
        this.shortdesc = shortdesc;
        this.rating = rating;
        this.sess_count = sess_count;
        this.credit_score = credit;
    }

    public String getId() {
        return sap_id;
    }

    public String getName() {
        return name;
    }

    public String getSubject() {
        return subject;
    }

    public String getShortdesc() {
        return shortdesc;
    }

    public double getRating() {
        return rating;
    }

    public double getCreditScore() {
        return credit_score;
    }

    public int getSessCount() {
        return sess_count;
    }
}
