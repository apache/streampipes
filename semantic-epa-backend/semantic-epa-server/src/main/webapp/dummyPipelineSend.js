function sendDummyPipeline(pipeline){
	
	$.post("http://localhost:8080/semantic-epa-backend/api/pipelines", JSON.stringify(pipeline));
	
	
}
