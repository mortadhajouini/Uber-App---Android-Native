package com.example.covoiturage;

public class Covoiturage {

    private String id;
    private String destination;
    private String departure;
    private String dateTime;
    private String price;

    public Covoiturage() {
    }

    public Covoiturage(String id, String destination, String departure, String dateTime, String price) {
        this.id = id;
        this.destination = destination;
        this.departure = departure;
        this.dateTime = dateTime;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
