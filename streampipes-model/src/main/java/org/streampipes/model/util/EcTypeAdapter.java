package org.streampipes.model.util;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.streampipes.model.impl.EcType;

import java.io.IOException;

public class EcTypeAdapter extends PeTypeAdapter<EcType> {

	@Override
	public void write(JsonWriter out, EcType value) throws IOException {
		write(out, value.getLabel(), value.getDescription(), value.name());
	}

	@Override
	public EcType read(JsonReader in) throws IOException {
		in.beginObject();
		while(in.hasNext()) {
			String name = in.nextString();
			if (name.equals("type"))
				return EcType.valueOf(name);
		}
		throw new IOException();
	}

	

}
