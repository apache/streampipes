package org.streampipes.pe.processors.esper;

import org.apache.commons.lang3.RandomStringUtils;

import com.google.common.io.Resources;

import org.streampipes.commons.exceptions.SepaParseException;
import org.streampipes.pe.processors.esper.extract.ProjectController;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.container.util.DeclarerUtils;

public class TestPerformance {

	public static void main(String[] args) throws InterruptedException
	{
		DataProcessorInvocation invocation;
		try {
			ProjectController controller = new ProjectController();
			invocation = DeclarerUtils.descriptionFromResources(Resources.getResource("test-invocation.jsonLd"), DataProcessorInvocation.class);
			controller.invokeRuntime(invocation);
			
			DataProcessorInvocation invocation2 = new DataProcessorInvocation(invocation);
			invocation2.getOutputStream().getEventGrounding().getTransportProtocol().setTopicName(RandomStringUtils.randomAlphabetic(8));
			
			controller.invokeRuntime(invocation2);
			Thread.sleep(2000);
			controller.detachRuntime(invocation.getElementId());
			Thread.sleep(2000);
			controller.detachRuntime(invocation2.getElementId());
			
		} catch (SepaParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}