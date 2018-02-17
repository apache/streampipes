export class ElementIconText {

    constructor() {}

    getElementIconText(s) {
        var result = "";
        if (s.length <= 4) {
            result = s;
        } else {
            var words = s.split(" ");
            words.forEach(function (word, i) {
                if (i < 4) {
                    result += word.charAt(0);
                }
            });
        }
        return result.toUpperCase();
    }
}
