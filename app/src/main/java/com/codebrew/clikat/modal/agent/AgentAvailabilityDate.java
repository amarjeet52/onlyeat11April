package com.codebrew.clikat.modal.agent;

import java.util.Date;

public class AgentAvailabilityDate implements Comparable<AgentAvailabilityDate> {

    private Date date;
    private String format_date;

    public AgentAvailabilityDate(Date date, String format_date) {
        this.date = date;
        this.format_date = format_date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getFormat_date() {
        return format_date;
    }

    public void setFormat_date(String format_date) {
        this.format_date = format_date;
    }

    @Override
    public int compareTo(AgentAvailabilityDate o) {
        return getDate().compareTo(o.getDate());
    }
}
