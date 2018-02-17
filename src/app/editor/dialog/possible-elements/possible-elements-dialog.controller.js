export class PossibleElementsController {

    constructor($mdDialog, ElementIconText, JsplumbService, pipelineModel, possibleElements, pipelineElementDomId) {
        this.$mdDialog = $mdDialog;
        this.ElementIconText = ElementIconText;
        this.pipelineModel = pipelineModel;
        this.pipelineElementDomId = pipelineElementDomId;
        this.possibleElements = possibleElements;
        this.JsplumbService = JsplumbService;
    }

    create(possibleElement) {
        this.JsplumbService.createElement(this.pipelineModel, possibleElement, this.pipelineElementDomId);
    }

    iconText(elementId) {
        return this.ElementIconText.getElementIconText(elementId);
    }

    hide() {
        this.$mdDialog.hide();
    };

    cancel() {
        this.$mdDialog.cancel();
    };
}

PossibleElementsController.$inject = ['$mdDialog', 'ElementIconText', 'JsplumbService', 'pipelineModel', 'possibleElements', 'pipelineElementDomId'];