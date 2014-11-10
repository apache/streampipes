function addToPipeline(element, pipeline){
	if ($(element).hasClass('action')){
		
		
		pipeline.action.DOM = element.id;
		pipeline.action.name = $(element).data("JSON").name;
		pipeline.action.elementId = $(element).data("JSON").elementId;
		pipeline.action.description = $(element).data("JSON").description;
		pipeline.action.connectedTo = [];
		for (var i = 0; i < jsPlumb.getConnections({
			target : element
		}).length; i++) {
			pipeline.action.connectedTo.push(jsPlumb.getConnections({target: element})[i].sourceId);
		}
		
	} else if ($(element).hasClass('sepa')){
		var el = {};
		el.DOM = element.id;
		el.name = $(element).data("JSON").name;
		el.description = $(element).data("JSON").description;
		el.staticProperties = $(element).data("JSON").staticProperties;
		el.domains = $(element).data("JSON").domains;
		el.elementId = $(element).data("JSON").elementId;
		
		el.connectedTo = [];
		
		for (var i = 0; i < jsPlumb.getConnections({
			target : element
		}).length; i++) {
			el.connectedTo.push(jsPlumb.getConnections({target: element})[i].sourceId);
		}
		pipeline.sepas.push(el);
	} else if ($(element).hasClass('stream')){
		
		var el = {};
		el.DOM = element.id;
		el.name = $(element).data("JSON").name;
		el.description = $(element).data("JSON").description;
		
		el.domains = $(element).data("JSON").domains;
		el.elementId = $(element).data("JSON").elementId;
		pipeline.streams.push(el);
		
	}
	
	// return pipeline
}
