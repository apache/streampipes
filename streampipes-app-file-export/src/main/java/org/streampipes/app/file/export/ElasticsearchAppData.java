package org.streampipes.app.file.export;

public class ElasticsearchAppData {

    private String index;

    private long timestampFrom;

    private long timeStampTo;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public long getTimestampFrom() {
        return timestampFrom;
    }

    public void setTimestampFrom(long timestampFrom) {
        this.timestampFrom = timestampFrom;
    }

    public long getTimeStampTo() {
        return timeStampTo;
    }

    public void setTimeStampTo(long timeStampT) {
        this.timeStampTo = timeStampT;
    }
}
