package de.fzi.cep.sepa.appstore.bundle;

import java.io.File;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

public class BundleContextUtil {

	public static final String STREAMPIPES_APP_NAME = "Streampipes-App-Name";
	public static final String STREAMPIPES_APP_DESCRIPTION = "Streampipes-App-Description";
	public static final String STREAMPIPES_APP_CONTEXT_PATH = "Streampipes-App-Context-Path";
	
	public static BundleContext getWorkingBundleContext( final BundleContext bc)
    {
        return bc.getBundle(Constants.SYSTEM_BUNDLE_LOCATION).getBundleContext();
        
    }
	
	public static boolean isStreamPipesApp(Bundle bundle) {
		return bundle.getHeaders().get(STREAMPIPES_APP_NAME) != null;
	}
	
	public static String getStreamPipesProperty(Bundle bundle, String property) {
		return bundle.getHeaders().get(property).toString();
	}
	
	public static File getBundleFileById(final BundleContext bc, long bundleId) {
		return new File(getBundleById(bc, bundleId).getLocation().replace("file:", ""));
	}
	
	public static Bundle getBundleById(final BundleContext bc, long bundleId) {
		return getWorkingBundleContext(bc).getBundle(bundleId);
	}
}
