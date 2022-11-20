package org.apache.streampipes.dataexplorer.v4.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;

public class TimeParser {

  private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
      .appendPattern("uuuu[-MM[-dd]]['T'HH[:mm[:ss[.SSSSSSSSS][.SSSSSSSS][.SSSSSSS][.SSSSSS][.SSSSS][.SSSS][.SSS][.SS][.S]]]][XXX]")
      .parseDefaulting(ChronoField.NANO_OF_SECOND, 0)
      .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
      .toFormatter();

  public static Long parseTime(String v) {
    TemporalAccessor temporalAccessor = formatter.parseBest(v,
        ZonedDateTime::from,
        LocalDateTime::from,
        LocalDate::from);

    Instant instant = Instant.from(temporalAccessor);
    return Instant.EPOCH.until(instant, ChronoUnit.MILLIS);
  }
}
