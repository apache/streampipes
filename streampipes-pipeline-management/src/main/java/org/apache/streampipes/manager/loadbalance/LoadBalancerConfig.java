package org.apache.streampipes.manager.loadbalance;

public class LoadBalancerConfig {

    // CPU usage weight when calculating new resource usage
    public static double CPUResourceWeigh = 1.0;

    // Memory usage weight when calculating new resource usage
    public static double MemoryResourceWeight = 1.0;

    public static double DirMemoryResourceWeight = 1.0;

    // Service resource usage threshold
    public static float ThresholdMigratorPercentage = 20.0F;

    public static float MinMigratorPercentage = 20.0F;

    public static float OverloadedThresholdPercentage = 85F;

    // History usage accounts for when calculating new resource usage
    public static float HistoryResourcePercentage = 0.9F;

    // Message-rate percentage threshold between highest and least loaded service for uniform load Migration
    public static int MsgRateDifferenceMigratorThreshold = 85;

    // Target standard deviation range
    public static float LoadTargetStd = 25.0F;


    public static String selector = "WeightedRandomSelector";

    public static String migrator="ThresholdMigrator";

}
