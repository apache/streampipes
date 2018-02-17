export default function pipelineCategoryFilter() { 
	return function(pipelines, categoryId) {
		console.log(categoryId);
		var result = [];
		var showAll = false;
		if (categoryId == "") showAll = true;
		angular.forEach(pipelines, function(pipeline) {
			if (showAll) result.push(pipeline);
			else {
				angular.forEach(pipeline.pipelineCategories, function(category) {
					if (category == categoryId) result.push(pipeline);
				})
			}
		})
		return result;
	};
};
