iframeAutoSize.$inject = [];

export default function iframeAutoSize() {

	return {
		restrict: 'A',
		link: function(scope, element, attrs) {
			element.on('load', function() {
				console.log(element[0]);
				var iFrameHeight = element[0].contentWindow.document.body.scrollHeight + 'px';
				element.css('height', iFrameHeight);
			});
		}
	}
};
