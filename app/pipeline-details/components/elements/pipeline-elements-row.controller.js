export class PipelineElementsRowController {

    constructor(ElementIconText) {
        this.ElementIconText = ElementIconText;
    }

    elementTextIcon() {
        return this.ElementIconText.getElementIconText(this.element.name);
    }

    getElementType(pipeline, element) {
        var elementType = "action";

        angular.forEach(pipeline.streams, el => {
            if (element.DOM == el.DOM) {
                elementType = "stream";
            }
        });

        angular.forEach(pipeline.sepas, el => {
            if (element.DOM == el.DOM) {
                elementType = "sepa";
            }
        });

        return elementType;
    }
}

PipelineElementsRowController.$inject = ['ElementIconText'];
