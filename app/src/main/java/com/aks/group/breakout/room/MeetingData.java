package com.aks.group.breakout.room;

import java.sql.Time;
import java.util.Date;

/**
 * Created by anujs on 10/15/2017.
 */

public class MeetingData {

    private Date startDate;
    private Date endDate;
    private Time startTime;
    private Time endTime;

    private MeetingData(Date startDate, Time startTime, Date endDate, Time endTime)
    {
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
    }
}
