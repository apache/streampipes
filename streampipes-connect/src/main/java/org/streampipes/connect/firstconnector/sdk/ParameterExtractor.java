package org.streampipes.connect.firstconnector.sdk;

import org.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.staticproperty.StaticProperty;

import java.util.List;

public class ParameterExtractor {
    private List<StaticProperty> list;

    public ParameterExtractor(List<StaticProperty> list) {
        this.list = list;
    }

    public String singleValue(String internalName) {
        return (((FreeTextStaticProperty) getStaticPropertyByName(internalName))
                .getValue());
    }

    private StaticProperty getStaticPropertyByName(String name)
    {
        for(StaticProperty p : list)
        {
            if (p.getInternalName().equals(name)) return p;
        }
        return null;
    }
}
