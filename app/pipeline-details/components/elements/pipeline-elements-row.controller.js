export class PipelineElementsRowController {

    constructor(getElementIconText) {
        this.getElementIconText = getElementIconText;
    }

    elementTextIcon() {
        return this.getElementIconText(this.element.name);
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

        console.log(elementType);
        return elementType;
    }
}

PipelineElementsRowController.$inject = ['getElementIconText'];
