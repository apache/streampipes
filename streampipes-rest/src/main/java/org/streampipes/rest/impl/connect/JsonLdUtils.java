package org.streampipes.rest.impl.connect;

import org.streampipes.commons.Utils;
import org.streampipes.empire.core.empire.annotation.InvalidRdfException;
import org.streampipes.serializers.jsonld.JsonLdTransformer;

import java.lang.reflect.InvocationTargetException;

public class JsonLdUtils {

    public static String toJsonLD(Object o) {
        JsonLdTransformer jsonLdTransformer = new JsonLdTransformer();
        String result = null;
        try {
            result = Utils.asString(jsonLdTransformer.toJsonLd(o));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidRdfException e) {
            e.printStackTrace();
        }

        return result;
    }
}
