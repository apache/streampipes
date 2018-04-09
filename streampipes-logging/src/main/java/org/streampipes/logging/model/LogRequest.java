package org.streampipes.logging.model;

public class LogRequest {

    String sourceID;

    String dateFrom;

    String dateTo;

    public String getsourceID() {
        return sourceID;
    }

    public void setSourceID(String source) {
        this.sourceID = source;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }
}
