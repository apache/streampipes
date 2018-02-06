export function CategoryAlreadyInPipelineFilter() {
	return function(pipelines, categoryId) {
		var result = [];
		angular.forEach(pipelines, function(pipeline) {
			var inPipeline = false;
			angular.forEach(pipeline.pipelineCategories, function(category) {
				if (category == categoryId) inPipeline = true;
			})
			if (!inPipeline) result.push(pipeline);
		})
		return result;
	};
};
