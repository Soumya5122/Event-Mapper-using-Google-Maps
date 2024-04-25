// Event.java
package com.example.eventmanager;
public class Event {

    private String event;
    private String eventName;
    private String location;
    private String date;

    public Event(String location, String event, String eventName, String date) {
        this.location = location;
        this.event = event;
        this.eventName = eventName;
        this.date = date;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}