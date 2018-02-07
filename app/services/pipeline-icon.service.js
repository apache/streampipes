pipelineElementIconService.$inject = ['$http', '$rootScope', 'ImageChecker', 'getElementIconText'];

export default function pipelineElementIconService($http, $rootScope, ImageChecker, getElementIconText) {

    var pipelineElementIconService = {};

    pipelineElementIconService.addImageOrTextIcon = function ($element, json, small, type) {
        var iconUrl = "";
        if (type == 'block' && json.streams != null && typeof json.streams !== 'undefined') {
            iconUrl = json.streams[0].iconUrl;
        } else {
            iconUrl = json.iconUrl;
        }
        ImageChecker.imageExists(iconUrl, function (exists) {
            if (exists) {
                var $img = $('<img>')
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
                var name = "";
                if (type == 'block' && json.streams != null && typeof json.streams !== 'undefined') {
                    name = json.streams[0].name;
                } else {
                    name = json.name;
                }
                var $span = $("<span>")
                    .text(getElementIconText(name) || "N/A")
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
        });
    }

    return pipelineElementIconService;
}