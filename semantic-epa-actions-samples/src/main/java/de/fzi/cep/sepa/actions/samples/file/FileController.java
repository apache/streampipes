package de.fzi.cep.sepa.actions.samples.file;

import de.fzi.cep.sepa.actions.config.ActionConfig;
import de.fzi.cep.sepa.actions.samples.ActionController;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.sdk.builder.DataSinkBuilder;
import de.fzi.cep.sepa.sdk.helpers.SupportedFormats;
import de.fzi.cep.sepa.sdk.helpers.SupportedProtocols;

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
