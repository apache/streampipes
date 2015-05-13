package de.fzi.cep.sepa.util;

import org.apache.camel.model.DataFormatDefinition;
import org.apache.camel.spi.DataFormat;
import org.apache.camel.spi.RouteContext;

public class ThriftDataFormatDefinition extends DataFormatDefinition {

	@Override
    protected DataFormat createDataFormat(RouteContext routeContext) {
        return new ThriftDataFormat();
    }
}
