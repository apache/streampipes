package org.apache.streampipes.model.loadbalancer;

public class ServiceLoadDataReport {
    public final static int cpuStandard = 1;
    public final static int memoryStandard = 1;

    public Usage CPU;
    public Usage Memory;
    public int Weight;

    public ServiceLoadDataReport() {
    }

    public ServiceLoadDataReport(Usage CPU, Usage memory, int weight) {
        this.CPU = CPU;
        Memory = memory;
        Weight = weight;
    }

    public void setWeight(int CPU, int memory){
        Weight = Math.min(CPU / cpuStandard, memory / memoryStandard);
    }

    public int getWeight() {
        return Weight;
    }

    public Usage getCPU() {
        return CPU;
    }

    public void setCPU(Usage CPU) {
        this.CPU = CPU;
    }

    public Usage getMemory() {
        return Memory;
    }

    public void setMemory(Usage memory) {
        Memory = memory;
    }

    @Override
    public String toString() {
        return "ServiceLoadDataReport{" +
                "CPU=" + CPU +
                ", Memory=" + Memory +
                ", Weight=" + Weight +
                '}';
    }
}
