package org.streampipes.serializers.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public abstract class PeTypeAdapter<T> extends TypeAdapter<T> {

  protected void write(JsonWriter out, String label, String description, String name) throws IOException {
    out.beginObject();
    out.name("type");
    out.value(name);
    out.name("label");
    out.value(label);
    out.name("description");
    out.value(description);
    out.endObject();
  }
}
