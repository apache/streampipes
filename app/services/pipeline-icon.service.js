export class PipelineElementIconService {

    constructor(ImageChecker, ElementIconText) {
        this.ImageChecker = ImageChecker;
        this.ElementIconText = ElementIconText;
    }

    addImageOrTextIcon($element, json, small, type) {
        let iconUrl = "";
        if (type == 'block' && json.streams != null && typeof json.streams !== 'undefined') {
            iconUrl = json.streams[0].iconUrl;
        } else {
            iconUrl = json.iconUrl;
        }
        this.ImageChecker.imageExists(iconUrl, exists => {
            if (exists) {
                let $img = $('<img>')
                    .attr("src", iconUrl)
                    .data("JSON", $.extend(true, {}, json));
                if (type == 'draggable') {
                    $img.addClass("draggable-img tt");
                } else if (type == 'connectable') {
                    $img.addClass('connectable-img tt');
                } else if (type == 'block') {
                    $img.addClass('block-img tt');
                } else if (type == 'recommended') {
                    $img.addClass('recommended-item-img tt');
                }
                $element.append($img);
            } else {
                let name = "";
                if (type == 'block' && json.streams != null && typeof json.streams !== 'undefined') {
                    name = json.streams[0].name;
                } else {
                    name = json.name;
                }
                let $span = $("<span>")
                    .text(this.ElementIconText.getElementIconText(name) || "N/A")
                    .attr(
                        {
                            "data-toggle": "tooltip",
                            "data-placement": "top",
                            "data-delay": '{"show": 1000, "hide": 100}',
                            title: name
                        })
                    .data("JSON", $.extend(true, {}, json));
                if (small) {
                    $span.addClass("element-text-icon-small")
                } else {
                    $span.addClass("element-text-icon")
                }
                $element.append($span);
            }
        })
    }

}

PipelineElementIconService.$inject = ['ImageChecker', 'ElementIconText'];