package de.fzi.cep.sepa.appstore.deployer.api;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.client.methods.ZeroCopyConsumer;
import org.apache.http.nio.protocol.BasicAsyncRequestProducer;

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
		 CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
		 httpclient.start();
		try {
//			InputStream is = Request.Get(bundle.getAppStoreUrl() +"/" +bundle.getBundleId() +"/install")
//				.execute()
//				.returnContent()
//				.asStream();
			
			String filePath = getBundleFilename(bundle.getLocation());

			ZeroCopyConsumer<File> consumer = new ZeroCopyConsumer<File>(new File(filePath)) {

				@Override
				protected File process(HttpResponse response, File file,
						ContentType contentType) throws Exception {
					return file;
				}
			};
			
			BasicAsyncRequestProducer producer = new BasicAsyncRequestProducer(new HttpHost("ipe-koi15.fzi.de", 8080), new HttpGet(bundle.getAppStoreUrl() +"/" +bundle.getBundleId() +"/install"));
			Future<File> future = httpclient.execute(producer, consumer, null);
			File result = future.get();
			
			httpclient.close();
			return gson.toJson(new InstallationStatus(bundle, true));
			
			
		} catch (IOException e) {
			e.printStackTrace();
			return gson.toJson(new InstallationStatus(bundle, false, e.getMessage()));
		} catch (InterruptedException e) {
			e.printStackTrace();
			return gson.toJson(new InstallationStatus(bundle, false, e.getMessage()));
		} catch (ExecutionException e) {
			return gson.toJson(new InstallationStatus(bundle, false, e.getMessage()));
		} 
		
	}
	
	@POST
	@Path("/uninstall")
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
