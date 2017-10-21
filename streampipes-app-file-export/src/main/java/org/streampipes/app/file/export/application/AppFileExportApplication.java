package org.streampipes.app.file.export.application;

import org.streampipes.app.file.export.impl.Elasticsearch;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;


public class AppFileExportApplication extends Application{

    @Override
    public Set<Class<?>> getClasses(){
        Set<Class<?>> apiClasses = new HashSet<>();

        //APIs
        apiClasses.add(Elasticsearch.class);

        return apiClasses;
    }
}
