package org.streampipes.serializers.json;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.streampipes.model.DataProcessorType;

import java.io.IOException;

public class EpaTypeAdapter extends PeTypeAdapter<DataProcessorType> {

	@Override
	public void write(JsonWriter out, DataProcessorType value) throws IOException {
		write(out, value.getLabel(), value.getDescription(), value.name());
	}

	@Override
	public DataProcessorType read(JsonReader in) throws IOException {
		DataProcessorType dataProcessorType = null;
		in.beginObject();
		while(in.hasNext()) {
			String name = in.nextName();
			if (name.equals("type"))
				dataProcessorType = DataProcessorType.valueOf(in.nextString());
		}
		in.endObject();
		if (dataProcessorType != null) return dataProcessorType;
		else throw new IOException();
	}

}
