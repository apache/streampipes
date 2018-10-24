package org.streampipes.logging.model;

public class LogRequest {

    private String sourceID;
    private String dateFrom;
    private String dateTo;

    public String getSourceID() {
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
