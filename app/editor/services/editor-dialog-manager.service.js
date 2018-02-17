import {CustomizeController} from "../dialog/customize-pipeline-element/customize.controller";
import {MatchingErrorController} from "../dialog/matching-error/matching-error.controller";
import {TopicSelectionDialog} from "../dialog/topic/topic-selection-modal.controller";
import {PossibleElementsController} from "../dialog/possible-elements/possible-elements-dialog.controller";
import {HelpDialogController} from "../dialog/help/help-dialog.controller";
import {SavePipelineController} from "../dialog/save-pipeline/save-pipeline.controller";

export class EditorDialogManager {

    constructor($mdDialog, DialogBuilder) {
        this.$mdDialog = $mdDialog;
        this.DialogBuilder = DialogBuilder;
    }

    showMatchingErrorDialog(elementData) {
        var dialogContent = this.DialogBuilder.getDialogTemplate(MatchingErrorController, 'app/editor/dialog/matching-error/matching-error.tmpl.html');
        dialogContent.locals = {
            elementData: elementData
        }
        this.$mdDialog.show(dialogContent);
    }

    showCustomizeDialog(elementData, sourceEndpoint, sepa) {
        var dialogContent = this.DialogBuilder.getDialogTemplate(CustomizeController, 'app/editor/dialog/customize-pipeline-element/customizeElementDialog.tmpl.html');
        dialogContent.locals = {
            elementData: elementData,
            sourceEndpoint: sourceEndpoint,
            sepa: sepa
        }
        this.$mdDialog.show(dialogContent);
    };

    showCustomizeStreamDialog(streamDescription) {
        var dialogContent = this.DialogBuilder.getDialogTemplate(TopicSelectionDialog, 'app/editor/dialog/topic/topic-selection-modal.tmpl.html');
        dialogContent.locals = {
            streamDescription: streamDescription
        }
        this.$mdDialog.show(dialogContent);
    }

    showSavePipelineDialog(pipelineNew) {
        var dialogContent = this.DialogBuilder.getDialogTemplate(SavePipelineController, 'app/editor/dialog/save-pipeline/submitPipelineModal.tmpl.html');
        dialogContent.locals = {
            pipeline: pipelineNew
        }
        this.$mdDialog.show(dialogContent);
    }

    openHelpDialog(pipelineElementPayload) {
        this.$mdDialog.show({
            controller: HelpDialogController,
            controllerAs: 'ctrl',
            templateUrl: 'app/editor/dialog/help/help-dialog.tmpl.html',
            parent: angular.element(document.body),
            clickOutsideToClose: true,
            locals: {
                pipelineElement: pipelineElementPayload,
            },
            bindToController: true
        })
    };

    openPossibleElementsDialog(rawPipelineModel, possibleElements, pipelineElementDomId) {
        this.$mdDialog.show({
            controller: PossibleElementsController,
            controllerAs: 'ctrl',
            templateUrl: 'app/editor/dialog/possible-elements/possible-elements-dialog.tmpl.html',
            parent: angular.element(document.body),
            clickOutsideToClose: true,
            bindToController: true,
            locals: {
                rawPipelineModel: rawPipelineModel,
                possibleElements: possibleElements,
                pipelineElementDomId: pipelineElementDomId
            }
        })
    };

    showToast(type, title, description) {
        this.$mdToast.show(
            this.$mdToast.simple()
                .textContent(title)
                .position("top right")
                .hideDelay(3000)
        );
    }

    showTutorialDialog() {
        var confirm = this.$mdDialog.confirm()
            .title('Welcome to StreamPipes!')
            .textContent('If you are new to StreamPipes, check out our user guide')
            .ok('Show tutorial')
            .cancel('Cancel');

        return this.$mdDialog.show(confirm);
    }

    showClearAssemblyDialog(ev) {
        var confirm = this.$mdDialog.confirm()
            .title('Clear assembly area?')
            .textContent('All pipeline elements in the assembly area will be removed.')
            .targetEvent(ev)
            .ok('Clear assembly')
            .cancel('Cancel');
        return this.$mdDialog.show(confirm);
    }
}

EditorDialogManager.$inject = ['$mdDialog', 'DialogBuilder'];