package com.codebrew.clikat.modal.other;

import java.util.List;

public class TimeSlot {

    private String headerName;
    private List<String> timeName;
    private int timeId;
    private boolean headerType;
    private int availablityStatus;

    public TimeSlot(String headerName, List<String> timeName, boolean headerType) {

        this.headerName = headerName;
        this.timeName=timeName;
        this.headerType = headerType;
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public int getTimeId() {
        return timeId;
    }

    public void setTimeId(int timeId) {
        this.timeId = timeId;
    }

    public int getAvailablityStatus() {
        return availablityStatus;
    }

    public void setAvailablityStatus(int availablityStatus) {
        this.availablityStatus = availablityStatus;
    }

    public boolean isHeaderType() {
        return headerType;
    }

    public void setHeaderType(boolean headerType) {
        this.headerType = headerType;
    }

    public List<String> getTimeName() {
        return timeName;
    }

    public void setTimeName(List<String> timeName) {
        this.timeName = timeName;
    }
}
