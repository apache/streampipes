package de.fzi.cep.sepa.appstore;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import de.fzi.cep.sepa.appstore.api.AppStore;

public class AppstoreApplication extends Application {

	@Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> allClasses = new HashSet<Class<?>>();
        allClasses.add(AppStore.class);
        
        return allClasses;
    }

}
