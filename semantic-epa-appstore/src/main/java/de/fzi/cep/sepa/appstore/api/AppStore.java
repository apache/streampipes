package de.fzi.cep.sepa.appstore.api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.osgi.framework.Bundle;

import com.google.gson.Gson;

import de.fzi.cep.sepa.appstore.Activator;
import de.fzi.cep.sepa.appstore.bundle.BundleContextUtil;
import de.fzi.cep.sepa.appstore.shared.BundleInfo;


@Path("/")
public class AppStore {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getAvailableApps() {

		List<BundleInfo> bundleInfo = new ArrayList<>();
		Bundle[] bundles = BundleContextUtil.getWorkingBundleContext(Activator.bc).getBundles();
		
		for(Bundle bundle : bundles) {
			if (BundleContextUtil.isStreamPipesApp(bundle))
					bundleInfo.add(new BundleInfo(bundle.getBundleId(), 
							bundle.getSymbolicName(), 
							bundle.getLocation(),
							bundle.getVersion().toString(),
							BundleContextUtil.getStreamPipesProperty(bundle, BundleContextUtil.STREAMPIPES_APP_NAME),
							BundleContextUtil.getStreamPipesProperty(bundle, BundleContextUtil.STREAMPIPES_APP_DESCRIPTION),
							BundleContextUtil.getStreamPipesProperty(bundle, BundleContextUtil.STREAMPIPES_APP_CONTEXT_PATH)));
		}
		
		return new Gson().toJson(bundleInfo);
		
	}
	
	@GET
	@Path("/{bundleId}/install")
	@Produces("application/zip")
	public Response getInstallationPackage(@PathParam("bundleId") long bundleId) {
		File f = BundleContextUtil.getBundleFileById(Activator.bc, bundleId);
		 return Response.ok(f)
		            .header("Content-Disposition",
		                    "attachment; filename=" +f.getName()).build();
	}
}
