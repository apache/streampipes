package de.fzi.cep.sepa.appstore.deployer;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import de.fzi.cep.sepa.appstore.deployer.api.AppDeployer;

public class AppDeployerApplication extends Application {

	@Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> allClasses = new HashSet<Class<?>>();
        allClasses.add(AppDeployer.class);
        
        return allClasses;
    }
}
