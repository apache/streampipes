export default function generatedElementImplementation() {
	return {
		restrict : 'E',
		templateUrl : './generated-element-implementation.tmpl.html',
		scope : {
			zipFile : "=",
			element : "=",
		},
		controller: function($scope, $element) {

			$scope.extractedFiles = [];
			$scope.currentFileName = "";
			$scope.currentFileContents = "";
			$scope.loadingCompleted = false;

			$scope.new_zip = new JSZip();

			$scope.new_zip.loadAsync($scope.zipFile)
				.then(function(zip) {

					angular.forEach(zip.files, function(file) {
						var filename = file.name;
						$scope.extractedFiles.push({"fileNameLabel" : $scope.getFileName(filename), 
							"fileNameDescription" : $scope.getDirectory(filename), 
							"fileName" : filename, 
							"fileContents" : file});
					})    
					console.log($scope.extractedFiles);
				});

			$scope.openFile = function(file) {
				$scope.loadingCompleted = false;
				$scope.currentFileName = file.fileName;
				file.fileContents.async("string")
					.then(function (content) {
						$scope.currentFileContents = content;
						$scope.loadingCompleted = true;
					});;
			}

			$scope.getLanguage = function(filename) {
				if (filename.endsWith("java")) return "java";
				else if (filename.endsWith("xml")) return "xml";
				else return "";
			}

			$scope.getFileName = function(filename) {
				if (/.+\\/gi.test(filename))
					return filename.replace(/.+\\/g, "");
				else if (/.+\//gi.test(filename))
				return filename.replace(/.+\//g, "");
				else
					return filename;
			}

			$scope.getDirectory = function(filename) {
				if (/.+\\/gi.test(filename)) {
					var directory = /.+\\/gi.exec(filename)[0];
					return directory.replace(/\\/g, "/");
				}
				else if (/.+\//gi.test(filename)) {
					var directory = /.+\//gi.exec(filename)[0];
					return directory.replace(/\//g, "/");
				}
				else return "/";
			}

			$scope.downloadZip = function() {
				$scope.openSaveAsDialog($scope.element.name +".zip", $scope.zipFile, "application/zip");
			}

			$scope.openSaveAsDialog = function(filename, content, mediaType) {
				var blob = new Blob([content], {type: mediaType});
				saveAs(blob, filename);
			}  	

		}
	}
};
