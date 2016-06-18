package de.fzi.cep.sepa.client.osgi.init;

	import java.util.Dictionary;
import java.util.Hashtable;

import javax.servlet.ServletException;

import org.glassfish.jersey.servlet.ServletContainer;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;

import de.fzi.cep.sepa.client.init.ModelSubmitter;

	public abstract class OsgiSubmitter extends ModelSubmitter implements BundleActivator {

		private BundleContext bc;
	    private ServiceTracker tracker;
	    private HttpService httpService = null;
	    
	    @Override
	    public synchronized void start(BundleContext bundleContext) throws Exception {
	        this.bc = bundleContext;
	        init();
	        this.tracker = new ServiceTracker(this.bc, HttpService.class.getName(), null) {

	            @Override
	            public Object addingService(ServiceReference serviceRef) {
	                httpService = (HttpService) super.addingService(serviceRef);
	                try {
	                	// TODO - temporary workaround
	                    // This is a workaround related to issue JERSEY-2093; grizzly (1.9.5) needs to have the correct context
	                    // classloader set
	                	ClassLoader myClassLoader = getClass().getClassLoader();
	                    ClassLoader originalContextClassLoader = Thread.currentThread().getContextClassLoader();
	                    try {
	                        Thread.currentThread().setContextClassLoader(myClassLoader);
	                        httpService.registerServlet(getContextPath(), new ServletContainer(), getJerseyServletParams(), null);
	                    } finally {
	                        Thread.currentThread().setContextClassLoader(originalContextClassLoader);
	                    }
					} catch (ServletException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NamespaceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	                return httpService;
	            }

	            @Override
	            public void removedService(ServiceReference ref, Object service) {
	                if (httpService == service) {
	                    unregisterServlets();
	                    httpService = null;
	                }
	                super.removedService(ref, service);
	            }
	        };

	        this.tracker.open();

	       
	    }

	    @Override
	    public synchronized void stop(BundleContext bundleContext) throws Exception {
	        this.tracker.close();
	    }
	   

	    private void unregisterServlets() {
	        if (this.httpService != null) {
	            httpService.unregister(getContextPath());
	        }
	    }

	    private Dictionary<String, String> getJerseyServletParams() {
	        Dictionary<String, String> jerseyServletParams = new Hashtable<>();
	        jerseyServletParams.put("javax.ws.rs.Application", JerseyApplication.class.getName());
	        return jerseyServletParams;
	    }
	    
	    public abstract String getContextPath();
	
}
