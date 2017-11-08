package org.streampipes.manager.execution.http;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.streampipes.commons.Utils;
import org.streampipes.model.InvocableSEPAElement;
import org.streampipes.model.client.pipeline.PipelineElementStatus;
import org.streampipes.serializers.jsonld.JsonLdTransformer;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;

public class HttpRequestBuilder {

	private InvocableSEPAElement payload;
	
	public HttpRequestBuilder(InvocableSEPAElement payload)
	{
		this.payload = payload;
	}
	
	public PipelineElementStatus invoke()
	{
		System.out.println("Invoking element: " +payload.getBelongsTo());
		try {
            Response httpResp = Request.Post(payload.getBelongsTo()).bodyForm(new BasicNameValuePair("json", jsonLd())).execute();
//			Response httpResp = Request.Post(payload.getBelongsTo()).bodyString(jsonLd(), ContentType.APPLICATION_JSON).execute();
			return handleResponse(httpResp);			
		} catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Internal Server Error: de.fzi.cep.sepa.manager.execution");
			return new PipelineElementStatus(payload.getBelongsTo(), payload.getName(), false, e.getMessage());
		}
	}
	
	public PipelineElementStatus detach()
	{
		try {
			Response httpResp = Request.Delete(payload.getUri()).execute();
			return handleResponse(httpResp);
		} catch(Exception e)
		{
			e.printStackTrace();
			return new PipelineElementStatus(payload.getBelongsTo(), payload.getName(), false, e.getMessage());
		}
	}
	
	private PipelineElementStatus handleResponse(Response httpResp) throws JsonSyntaxException, ClientProtocolException, IOException
	{
		String resp = httpResp.returnContent().asString();
		org.streampipes.model.impl.Response streamPipesResp = new Gson().fromJson(resp, org.streampipes.model.impl.Response.class);
		return convert(streamPipesResp);
	}
	
	private String jsonLd() throws Exception
	{
		return Utils.asString(new JsonLdTransformer().toJsonLd(payload));
	}
	
	private PipelineElementStatus convert(org.streampipes.model.impl.Response response)
	{
		return new PipelineElementStatus(payload.getBelongsTo(), payload.getName(), response.isSuccess(), response.getOptionalMessage());
	}
}
