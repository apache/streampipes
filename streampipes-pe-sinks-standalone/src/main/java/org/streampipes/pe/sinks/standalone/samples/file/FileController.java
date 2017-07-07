package org.streampipes.pe.sinks.standalone.samples.file;

import org.streampipes.pe.sinks.standalone.config.ActionConfig;
import org.streampipes.pe.sinks.standalone.samples.ActionController;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.graph.SecDescription;
import org.streampipes.model.impl.graph.SecInvocation;
import org.streampipes.model.impl.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.sdk.builder.DataSinkBuilder;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;

public class FileController extends ActionController {

	private FileWriter fileWriter;

	@Override
	public SecDescription declareModel() {
		 SecDescription desc = DataSinkBuilder
				.create("file",  "File Output", "Writes the data in a csv file on the host system. The file name must be provided.")
				 .iconUrl(ActionConfig.iconBaseUrl + "/file_icon.png")
                 .requiredStaticProperty(new FreeTextStaticProperty("path", "Path", "The path to the file including the file name"))
                 .setStream1()
				 .supportedFormats(SupportedFormats.jsonFormat())
				 .supportedProtocols(SupportedProtocols.jms())
				.build();
		
		return desc;
	}



	@Override
	public Response invokeRuntime(SecInvocation sec) {
		String brokerUrl = createJmsUri(sec);
		String inputTopic = sec.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getTopicName();
		
		String path = ((FreeTextStaticProperty) (SepaUtils
				.getStaticPropertyByInternalName(sec, "path"))).getValue();
		
		FileParameters fileParameters = new FileParameters(inputTopic, brokerUrl, path);

		fileWriter = new FileWriter(fileParameters);
		new Thread(fileWriter).start();
	    String pipelineId = sec.getCorrespondingPipeline();
        return new Response(pipelineId, true);

	}


    @Override
    public Response detachRuntime(String pipelineId) {
		fileWriter.close();
        return new Response(pipelineId, true);
    }

	@Override
	public boolean isVisualizable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getHtml(SecInvocation invocation) {
		// TODO Auto-generated method stub
		return null;
	}

}
