package org.apache.streampipes.extensions.management.monitoring;


import com.sun.management.OperatingSystemMXBean;
import org.apache.streampipes.commons.prometheus.service.ElementServiceStats;
import org.apache.streampipes.extensions.management.init.DeclarersSingleton;
import org.apache.streampipes.model.loadbalancer.ServiceLoadDataReport;
import org.apache.streampipes.model.loadbalancer.Usage;

import java.lang.management.ManagementFactory;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServiceLoadDataReportGenerator {
    private static final int CPU_CHECK_MILLIS = 100;
    private static final double totalCPULimit;
    private static final OperatingSystemMXBean systemBean;
    private static ServiceLoadDataReport serviceLoadDataReport;;

    private static final ScheduledExecutorService executorService;
    private static double CPUUsageSum = 0d;
    private static double CPUUsageCount = 0;

    public static double CPUResourceWeigh = 100000.0;

    public static double MemoryResourceWeight = 100000.0;

    static {
        executorService = Executors.newSingleThreadScheduledExecutor();
        systemBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        serviceLoadDataReport = new ServiceLoadDataReport();
        totalCPULimit = getTotalCPULimit();

       // serviceLoadDataReport.setMemory(getMemoryUsage());
        calculateUsage();
        // 高频采集 CPU 瞬时值
        executorService.scheduleWithFixedDelay(ServiceLoadDataReportGenerator::checkCPULoad, CPU_CHECK_MILLIS, CPU_CHECK_MILLIS, TimeUnit.MILLISECONDS);
        // 每分钟汇总一次并写入 Prometheus 指标（调用 collectMetricsNow）
        executorService.scheduleWithFixedDelay(() -> {
            try {
                calculateUsage();
                collectMetricsNow();
            } catch (Exception ignored) {
            }
        }, 1, 1, TimeUnit.MINUTES);
    }

    public static ServiceLoadDataReport generateReport(){
        //serviceLoadDataReport.setMemory(getMemoryUsage());
        return serviceLoadDataReport;
    }

    /**
     * 基于当前 ServiceLoadDataReport 立即写入 Prometheus 指标。
     * 如需强制刷新，可先手动调用 calculateUsage() 再调用此方法。
     */
    public static void collectMetricsNow() {
        ServiceLoadDataReport r = generateReport();
        if (r == null || r.getCPU() == null || r.getMemory() == null) {
            return;
        }
        // 调试：打印原始值与百分比
        System.out.println("DEBUG collectMetricsNow: serviceId=" + DeclarersSingleton.getInstance().getServiceId());
        System.out.println("DEBUG CPU: usage=" + r.getCPU().usage + ", limit=" + r.getCPU().limit + ", percent=" + r.getCPU().percentUsage());
        System.out.println("DEBUG Memory: usage=" + r.getMemory().usage + ", limit=" + r.getMemory().limit + ", percent=" + r.getMemory().percentUsage());
        System.out.println("DEBUG Weight: " + r.getWeight());
        ElementServiceStats.metricsByReport(
                DeclarersSingleton.getInstance().getServiceId(),
                (double) 100,
                (double) 100,
                r.getWeight()
        );
        ElementServiceStats.metrics();
    }



    public static synchronized void checkCPULoad() {
        double cpuLoad = systemBean.getCpuLoad();
        if (!Double.isNaN(cpuLoad)) {
            CPUUsageSum += cpuLoad;
            CPUUsageCount++;
        }
    }

    public static void calculateUsage() {
        checkCPULoad();
        doCalculateUsage();
        doCalculateMemoryUsage();
        doCalculateWeight();
    }

    static void doCalculateUsage() {
        ServiceLoadDataReport serviceLoadDataReport = new ServiceLoadDataReport();
        serviceLoadDataReport.setCPU(getCPUUsage());
        serviceLoadDataReport.setMemory(getMemoryUsage());
        serviceLoadDataReport.setWeight((int) serviceLoadDataReport.getCPU().percentUsage(), (int) serviceLoadDataReport.getMemory().percentUsage());
        ServiceLoadDataReportGenerator.serviceLoadDataReport = serviceLoadDataReport;
        ElementServiceStats.metricsByReport(
                DeclarersSingleton.getInstance().getServiceId(),
                (double) 100,
                (double) 100,
                serviceLoadDataReport.getWeight()
        );
    }

    static void doCalculateMemoryUsage() {
        serviceLoadDataReport.setMemory(getMemoryUsage());
    }

    static void doCalculateWeight() {
        serviceLoadDataReport.setWeight((int) serviceLoadDataReport.getCPU().percentUsage(), (int) serviceLoadDataReport.getMemory().percentUsage());
    }

    private static double getTotalCPULimit(){
        return 100 * Runtime.getRuntime().availableProcessors();
    }

    private static synchronized double getTotalCPUUsage() {
        if (CPUUsageCount == 0) {
           return 0;
        }
        double CPUUsage = CPUUsageSum / CPUUsageCount;
        System.out.println("DEBUG getTotalCPUUsage: CPUUsageSum=" + CPUUsageSum + ", CPUUsageCount=" + CPUUsageCount + ", result=" + CPUUsage);
        CPUUsageSum = 0d;
        CPUUsageCount = 0;
        return CPUUsage;
    }

    private static Usage getCPUUsage() {
        double rawUsage = getTotalCPUUsage();
        double scaledUsage = rawUsage * totalCPULimit;
        System.out.println("DEBUG getCPUUsage: rawUsage=" + rawUsage + ", totalCPULimit=" + totalCPULimit + ", scaledUsage=" + scaledUsage);
        return new Usage(scaledUsage, totalCPULimit);
    }

    private static Usage getMemoryUsage() {
        double total = ((double) systemBean.getTotalMemorySize() / (1024 * 1024));
        double free = ((double) systemBean.getFreeMemorySize() / (1024 * 1024));
        System.out.println("DEBUG getMemoryUsage: total=" + total + ", free=" + free + ", used=" + (total - free));
        return new Usage(total - free, total);
    }
}
