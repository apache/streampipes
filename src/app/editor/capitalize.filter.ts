capitalize.$inject = [];

export default function capitalize() {
	return function(input) {
		if (input!=null) {
			input = input.replace(/_/g, " ");
			var stringArr = input.split(" ");
			var result = "";
			var cap = stringArr.length;
			for(var x = 0; x < cap; x++) {
				stringArr[x] = stringArr[x].toLowerCase();
				if(x === cap - 1) {
					result += stringArr[x].substring(0,1).toUpperCase() + stringArr[x].substring(1);
				} else {
					result += stringArr[x].substring(0,1).toUpperCase() + stringArr[x].substring(1) + " ";
				}
			}
			return result;
		}
	}
};
