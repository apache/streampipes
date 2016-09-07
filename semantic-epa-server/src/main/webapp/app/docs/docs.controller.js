DocsController.$inject = ['$scope'];

export default function DocsController($scope) {

	$scope.markdownDir = "app/docs/markdown/";

	$scope.currentMarkdownFile = $scope.markdownDir +"introduction.md";

	$scope.tabs = [
		{
			title : 'Introduction',
			type: 'intro'
		},
		{
			title : 'Building Pipelines',
			type: 'pipelines'
		},
		{
			title : 'Creating New Elements',
			type: 'create'
		}
	];

	$scope.loadCurrentElements = function(type) {
		if (type == 'intro')  { $scope.currentMarkdownFile = $scope.markdownDir +"introduction.md";  }
		else if (type == 'pipelines') {$scope.currentMarkdownFile = $scope.markdownDir +"pipelines.md";  }
		else if (type == 'create') { $scope.currentMarkdownFile = $scope.markdownDir +"create.md"; }
	}
};
