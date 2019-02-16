export class PossibleElementsController {

    $mdDialog: any;
    ElementIconText: any;
    rawPipelineModel: any;
    pipelineElementDomId: any;
    possibleElements: any;
    JsplumbService: any;

    constructor($mdDialog, ElementIconText, JsplumbService, rawPipelineModel, possibleElements, pipelineElementDomId) {
        this.$mdDialog = $mdDialog;
        this.ElementIconText = ElementIconText;
        this.rawPipelineModel = rawPipelineModel;
        this.pipelineElementDomId = pipelineElementDomId;
        this.possibleElements = possibleElements;
        this.JsplumbService = JsplumbService;
    }

    create(possibleElement) {
        this.JsplumbService.createElement(this.rawPipelineModel, possibleElement, this.pipelineElementDomId);
        this.hide();
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

PossibleElementsController.$inject = ['$mdDialog', 'ElementIconText', 'JsplumbService', 'rawPipelineModel', 'possibleElements', 'pipelineElementDomId'];