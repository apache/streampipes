package org.apache.streampipes.model.loadbalancer;

public class Usage {
    public final double usage;

    public final double limit;

    public Usage(double usage, double limit) {
        this.usage = usage;
        this.limit = limit;
    }
    public Usage(){
        this(0,0);
    }

    public float percentUsage() {
        float proportion = 0;
        if (limit > 0) {
           proportion = ((float) usage) / ((float) limit);
        }
        return proportion * 100;
    }

    public int getUsageInt() {
        return (int) usage;
    }

    @Override
    public String toString() {
        return "Usage{" +
                "usage=" + usage +
                ", limit=" + limit +
                '}';
    }
}
