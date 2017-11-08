package org.streampipes.serializers.json;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.streampipes.model.impl.EpaType;

import java.io.IOException;

public class EpaTypeAdapter extends PeTypeAdapter<EpaType> {

	@Override
	public void write(JsonWriter out, EpaType value) throws IOException {
		write(out, value.getLabel(), value.getDescription(), value.name());
	}

	@Override
	public EpaType read(JsonReader in) throws IOException {
		EpaType epaType = null;
		in.beginObject();
		while(in.hasNext()) {
			String name = in.nextName();
			if (name.equals("type"))
				epaType = EpaType.valueOf(in.nextString());
		}
		in.endObject();
		if (epaType != null) return epaType;
		else throw new IOException();
	}

}
