export default function getElementIconText() {
	return function(string) {
		var result = "";
		if (string.length <= 4) {
			result = string;
		} else {
			var words = string.split(" ");
			words.forEach(function (word, i) {
				if (i < 4) {
					result += word.charAt(0);
				}
			});
		}
		return result.toUpperCase();
	}
};
