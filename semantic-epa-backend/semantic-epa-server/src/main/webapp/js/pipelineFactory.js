function addToPipeline(element, pipeline){
	console.log(element);
	if ($(element).hasClass('action')){


		pipeline.action.DOM = element.id;
		pipeline.action.name = $(element).data("JSON").name;
		pipeline.action.elementId = $(element).data("JSON").elementId;
		pipeline.action.description = $(element).data("JSON").description;
		pipeline.action.iconUrl = $(element).data("JSON").iconUrl;
		pipeline.action.connectedTo = [];
		pipeline.action.elementId = $(element).data("JSON").elementId;
		pipeline.action.staticProperties = $(element).data("JSON").staticProperties;
		for (var i = 0; i < jsPlumb.getConnections({
			target : element
		}).length; i++) {
			pipeline.action.connectedTo.push(jsPlumb.getConnections({target: element})[i].sourceId);
		}
	}else{
		var el = {};
		el.DOM = element.id;
		el.name = $(element).data("JSON").name;
		el.iconUrl = $(element).data("JSON").iconUrl;
		el.description = $(element).data("JSON").description;
		el.domains = $(element).data("JSON").domains;
		el.elementId = $(element).data("JSON").elementId;

	 	if ($(element).hasClass('sepa')){

			el.staticProperties = $(element).data("JSON").staticProperties;
			el.connectedTo = [];
			for (var i = 0; i < jsPlumb.getConnections({
				target : element
			}).length; i++) {
				el.connectedTo.push(jsPlumb.getConnections({target: element})[i].sourceId);
			}
			pipeline.sepas.push(el);

		} else if ($(element).hasClass('stream')){

			pipeline.streams.push(el);
			
		}
	
	}
}
