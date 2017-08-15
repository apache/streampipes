package org.streampipes.pe.mixed.flink.extensions;

import org.apache.flink.api.common.functions.Function;

import java.io.Serializable;

/**
 * Created by riemer on 12.04.2017.
 */
public interface TimestampMappingFunction<IN> extends Function, Serializable {

  Long getTimestamp(IN in);
}
