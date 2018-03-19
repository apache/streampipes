export function startsWithLetter() {
	return function (items, fromLetter, toLetter) {
		var filtered = [];
		for (var i = 0; i < items.length; i++) {
			var item = items[i];
			var firstLetter = item.name.substring(0, 1).toLowerCase();
			if ((!fromLetter || firstLetter >= fromLetter)
				&& (!toLetter || firstLetter <= toLetter)) {
					filtered.push(item);
				}
		}
		return filtered;
	};
};
