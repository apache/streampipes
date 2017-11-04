package org.streampipes.app.file.export;

public class ElasticsearchAppData {

    private String index;

    private long timestampFrom;

    private long timestampTo;

    public ElasticsearchAppData() {
    }

    public ElasticsearchAppData(String index, long timestampFrom, long timeStampTo) {
        this.index = index;
        this.timestampFrom = timestampFrom;
        this.timestampTo = timestampTo;
    }

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

    public long getTimestampTo() {
        return timestampTo;
    }


    public void setTimeStampTo(long timestampTo) {
        this.timestampTo = timestampTo;
    }
}
