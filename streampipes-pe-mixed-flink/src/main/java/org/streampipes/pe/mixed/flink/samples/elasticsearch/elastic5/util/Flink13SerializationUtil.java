package org.streampipes.pe.mixed.flink.samples.elasticsearch.elastic5.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Created by riemer on 21.03.2017.
 */
public class Flink13SerializationUtil {

  private static byte[] serializeObject(Object o) throws IOException {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(baos)) {
      oos.writeObject(o);
      oos.flush();
      return baos.toByteArray();
    }
  }

  public static boolean isSerializable(Object o) {
    try {
      serializeObject(o);
    } catch (IOException e) {
      return false;
    }

    return true;
  }
}
