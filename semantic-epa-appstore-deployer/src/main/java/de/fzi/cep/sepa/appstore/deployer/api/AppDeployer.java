package de.fzi.cep.sepa.appstore.deployer.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;

import com.google.gson.Gson;

import de.fzi.cep.sepa.appstore.shared.BundleInfo;
import de.fzi.cep.sepa.appstore.shared.InstallationStatus;
import de.fzi.cep.sepa.appstore.shared.UninstallStatus;
import de.fzi.cep.sepa.commons.config.ClientConfiguration;

@Path("/")
public class AppDeployer {

	private final static String POD_PATH = ClientConfiguration.INSTANCE.getPodDeploymentDirectory();
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String installApp(String bundleString) {
		Gson gson = new Gson();
		BundleInfo bundle = gson.fromJson(bundleString, BundleInfo.class);
		try {
			InputStream is = Request.Get(bundle.getAppStoreUrl() +"/" +bundle.getBundleId() +"/install")
				.execute()
				.returnContent()
				.asStream();
			String filePath = getBundleFilename(bundle.getLocation());

			FileOutputStream fos = new FileOutputStream(filePath);
			int inByte;
			while((inByte = is.read()) != -1)
			     fos.write(inByte);
			is.close();
			fos.close();
			return gson.toJson(new InstallationStatus(bundle, true));
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return gson.toJson(new InstallationStatus(bundle, false, e.getMessage()));
		} catch (IOException e) {
			e.printStackTrace();
			return gson.toJson(new InstallationStatus(bundle, false, e.getMessage()));
		}
		
	}
	
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String uninstallApp(String bundleString) {
		Gson gson = new Gson();
		BundleInfo bundle = gson.fromJson(bundleString, BundleInfo.class);
		String filePath = getBundleFilename(bundle.getLocation());
		
		File bundleFile = new File(filePath);
		if (bundleFile.delete()) return gson.toJson(new UninstallStatus(true));
		else return gson.toJson(new UninstallStatus(false));
	}
	
	private String getBundleFilename(String bundleLocation) {
		return POD_PATH +bundleLocation.replaceFirst(".+\\/", "");
	}
}
