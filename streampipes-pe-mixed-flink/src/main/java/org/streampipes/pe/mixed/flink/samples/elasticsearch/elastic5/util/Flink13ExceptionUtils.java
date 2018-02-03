package org.streampipes.pe.mixed.flink.samples.elasticsearch.elastic5.util;

public class Flink13ExceptionUtils {

  public static boolean containsThrowable(Throwable throwable, Class<?> searchType) {
    if (throwable == null || searchType == null) {
      return false;
    }

    Throwable t = throwable;
    while (t != null) {
      if (searchType.isAssignableFrom(t.getClass())) {
        return true;
      } else {
        t = t.getCause();
      }
    }

    return false;
  }
}
