package com.aks.group.breakout.room;

/**
 * Created by anujs on 5/26/2017.
 */

public class EventsData {
    private String eventUid;
    private String eventName;
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    public EventsData() {
    }

    public EventsData(String eventUid, String eventName, String startDate, String starteTime, String endDate, String endTime) {
        this.eventUid = eventUid;
        this.eventName = eventName;
        this.startDate = startDate;
        this.startTime = starteTime;
        this.endDate = endDate;
        this.endTime = endTime;
    }

    public void setEventUid(String eventUid) { this.eventUid = eventUid; }
    public String getEventUid() { return this.eventUid; }

    public void setEventName(String eventUid) { this.eventName = eventName; }
    public String getEventName() { return this.eventName; }

    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getStartDate() { return this.startDate; }

    public void setStartTime(String startDate) { this.startTime = startTime; }
    public String getStartTime() { return this.startTime; }

    public void setEndDate(String endDate) { this.endDate = endDate; }
    public String getEndDate() { return this.endDate; }

    public void setEndTime(String endTime) { this.endTime = endTime; }
    public String getEndTime() { return this.endTime; }

}
