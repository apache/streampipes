	export default function imageChecker () {
		var imageChecker = {};

		imageChecker.imageExists = function(url, callback) {
			if (url == null || url === 'undefined'){
				callback(false);
				return;
			}
			var img = new Image();
			img.onload = function() { callback(true); };
			img.onerror = function() { callback(false); };
			img.src = url;
		};
		imageChecker.imageExistsBoolean = function(url){
			if (url == null || url === 'undefined'){
				return false;
			}
			var img = new Image();
			img.onload = function() { callback(true); };
			img.onerror = function() { callback(false); };
			img.src = url;
		}

		return imageChecker;
	};
