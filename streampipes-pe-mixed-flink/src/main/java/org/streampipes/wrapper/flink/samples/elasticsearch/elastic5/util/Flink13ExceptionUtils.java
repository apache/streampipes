package org.streampipes.wrapper.flink.samples.elasticsearch.elastic5.util;

/**
 * Created by riemer on 21.03.2017.
 */
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
