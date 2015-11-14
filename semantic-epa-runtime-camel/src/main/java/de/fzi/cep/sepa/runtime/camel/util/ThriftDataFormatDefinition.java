package de.fzi.cep.sepa.runtime.camel.util;

import org.apache.camel.model.DataFormatDefinition;
import org.apache.camel.spi.DataFormat;
import org.apache.camel.spi.RouteContext;

public class ThriftDataFormatDefinition extends DataFormatDefinition {

	@Override
    protected DataFormat createDataFormat(RouteContext routeContext) {
        return new ThriftDataFormat();
    }
}
