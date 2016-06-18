package de.fzi.cep.sepa.client.osgi.init;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import de.fzi.cep.sepa.client.api.SecElement;
import de.fzi.cep.sepa.client.api.SepElement;
import de.fzi.cep.sepa.client.api.SepaElement;
import de.fzi.cep.sepa.client.api.WelcomePage;



public class JerseyApplication extends Application {
	
	@Override
	public Set<Object> getSingletons() {
		Set<Object> objects = new HashSet<>();
		objects.add(new SecElement());
		objects.add(new SepaElement());
		objects.add(new WelcomePage());
		objects.add(new SepElement());
		return objects;
	}
	
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> allClasses = new HashSet<Class<?>>();
//        allClasses.add(SecElement.class);
//        allClasses.add(SepaElement.class);
//        allClasses.add(SepElement.class);
//        allClasses.add(WelcomePage.class);
//        allClasses.add(TestResource.class);
        return allClasses;
    }

}
