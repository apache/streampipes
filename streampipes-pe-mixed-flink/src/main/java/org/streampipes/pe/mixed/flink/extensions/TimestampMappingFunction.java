package org.streampipes.pe.mixed.flink.extensions;

import org.apache.flink.api.common.functions.Function;

import java.io.Serializable;

public interface TimestampMappingFunction<IN> extends Function, Serializable {

  Long getTimestamp(IN in);
}
