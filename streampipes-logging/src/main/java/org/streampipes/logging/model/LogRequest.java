package org.streampipes.logging.model;

public class LogRequest {

    String source;

    String dateFrom;

    String dateTo;

    public String getsource() {
        return source;
    }

    public void setsource(String source) {
        this.source = source;
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
