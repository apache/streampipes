package de.fzi.cep.sepa.manager.execution.http;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.client.pipeline.PipelineElementStatus;
import de.fzi.cep.sepa.model.transform.JsonLdTransformer;
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
		de.fzi.cep.sepa.model.impl.Response streamPipesResp = new Gson().fromJson(resp, de.fzi.cep.sepa.model.impl.Response.class);
		return convert(streamPipesResp);
	}
	
	private String jsonLd() throws Exception
	{
		return Utils.asString(new JsonLdTransformer().toJsonLd(payload));
	}
	
	private PipelineElementStatus convert(de.fzi.cep.sepa.model.impl.Response response)
	{
		return new PipelineElementStatus(payload.getBelongsTo(), payload.getName(), response.isSuccess(), response.getOptionalMessage());
	}
}
