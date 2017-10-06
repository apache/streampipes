package org.streampipes.app.file.export.application;

import javax.ws.rs.core.Application;
import javafx.stage.Stage;
import org.streampipes.app.file.export.impl.Elasticsearch;

import java.util.HashSet;
import java.util.Set;

public class AppFileExportApplication extends Application{

    @Override
    public Set<Class<?>> getClasses(){
        Set<Class<?>> apiClasses = new HashSet<>();

        //APIs
        apiClasses.add(Elasticsearch.class);

        return apiClasses;
    }
}
