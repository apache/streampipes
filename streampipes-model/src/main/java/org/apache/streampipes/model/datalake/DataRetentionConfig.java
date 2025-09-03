package org.apache.streampipes.model.datalake;

public record DataRetentionConfig(RetentionInterval interval, int olderThanDays, RetentionAction action) {}