package de.fzi.cep.sepa.manager.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.openrdf.model.impl.GraphImpl;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;
import de.fzi.cep.sepa.storage.util.Transformer;

public class HttpRequestBuilder {

	
	SEPAInvocationGraph payload;
	
	public HttpRequestBuilder(SEPAInvocationGraph payload)
	{
		this.payload = payload;
	}
	
	public boolean invoke()
	{
		try {
		    HttpParams params = new BasicHttpParams();
		    params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
		    HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		    HttpClient httpclient = new DefaultHttpClient(params);
		    HttpPost httppost = new HttpPost(payload.getUri());
		        
		    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		    nameValuePairs.add(new BasicNameValuePair("json", Utils.asString(Transformer.generateCompleteGraph(new GraphImpl(), payload))));
		    
		    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
		    
		    HttpResponse response = httpclient.execute(httppost);
		    HttpEntity entity = response.getEntity();
		    if (entity != null)
		    {
		      System.out.println(EntityUtils.toString(entity));
		    }
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}
	
	
	public boolean detach()
	{
	return true;	
	}
}
