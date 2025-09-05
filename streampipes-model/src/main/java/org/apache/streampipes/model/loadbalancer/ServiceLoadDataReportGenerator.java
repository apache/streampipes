package org.apache.streampipes.model.loadbalancer;


import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServiceLoadDataReportGenerator {
    private static final int CPU_CHECK_MILLIS = 100;
    private static final double totalCPULimit;
    private static final OperatingSystemMXBean systemBean;
    private static final ServiceLoadDataReport serviceLoadDataReport;;

    private static final ScheduledExecutorService executorService;
    private static double CPUUsageSum = 0d;
    private static double CPUUsageCount = 0;

    static {
        executorService = Executors.newSingleThreadScheduledExecutor();
        systemBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        serviceLoadDataReport = new ServiceLoadDataReport();
        totalCPULimit = getTotalCPULimit();

        serviceLoadDataReport.setMemory(getMemoryUsage());
        
        // 启动定时任务
        executorService.scheduleWithFixedDelay(ServiceLoadDataReportGenerator::checkCPULoad, CPU_CHECK_MILLIS, CPU_CHECK_MILLIS, TimeUnit.MILLISECONDS);
        executorService.scheduleWithFixedDelay(ServiceLoadDataReportGenerator::doCalculateUsage, 1, 1, TimeUnit.MINUTES);
        executorService.scheduleWithFixedDelay(ServiceLoadDataReportGenerator::doCalculateMemoryUsage, 1, 1, TimeUnit.MINUTES);
        executorService.scheduleWithFixedDelay(ServiceLoadDataReportGenerator::doCalculateWeight, 1, 1, TimeUnit.MINUTES);
    }

    public static ServiceLoadDataReport generateReport(){
        serviceLoadDataReport.setMemory(getMemoryUsage());
        return serviceLoadDataReport;
    }

    public static synchronized void checkCPULoad() {
        double cpuLoad = systemBean.getCpuLoad();
        if (!Double.isNaN(cpuLoad)) {
            CPUUsageSum += cpuLoad;
            CPUUsageCount++;
        }
    }

    static void doCalculateUsage() {
        serviceLoadDataReport.setCPU(getCPUUsage());
    }

    static void doCalculateMemoryUsage() {
        serviceLoadDataReport.setMemory(getMemoryUsage());
    }

    static void doCalculateWeight() {
        serviceLoadDataReport.setWeight(serviceLoadDataReport.getCPU().getUsageInt(), serviceLoadDataReport.getMemory().getUsageInt());
    }

    private static double getTotalCPULimit(){
        return 100 * Runtime.getRuntime().availableProcessors();
    }

    private static synchronized double getTotalCPUUsage() {
        if (CPUUsageCount == 0) {
           return 0;
        }
        double CPUUsage = CPUUsageSum / CPUUsageCount;
        CPUUsageSum = 0d;
        CPUUsageCount = 0;
        return CPUUsage;
    }

    private static Usage getCPUUsage() {
        return new Usage(getTotalCPUUsage() * totalCPULimit,totalCPULimit);
    }

    private static Usage getMemoryUsage() {
        double total = ((double) systemBean.getTotalMemorySize() / (1024 * 1024));
        double free = ((double) systemBean.getFreeMemorySize() / (1024 * 1024));
        return new Usage(total - free, total);
    }
}
