import * as angular from 'angular';

declare const require: any;

import {CustomizeController} from "../dialog/customize-pipeline-element/customize.controller";
import {MatchingErrorController} from "../dialog/matching-error/matching-error.controller";
import {TopicSelectionDialog} from "../dialog/topic/topic-selection-modal.controller";
import {PossibleElementsController} from "../dialog/possible-elements/possible-elements-dialog.controller";
import {HelpDialogController} from "../dialog/help/help-dialog.controller";
import {SavePipelineController} from "../dialog/save-pipeline/save-pipeline.controller";
import {WelcomeTourDialogController} from "../dialog/welcome-tour/welcome-tour-dialog.controller";
import {MissingElementsForTutorialDialogController} from "../dialog/missing-elements-for-tutorial/missing-elements-for-tutorial-dialog.controller";

export class EditorDialogManager {

    $mdDialog: any;
    DialogBuilder: any;
    $mdToast: any;

    constructor($mdDialog, DialogBuilder) {
        this.$mdDialog = $mdDialog;
        this.DialogBuilder = DialogBuilder;
    }

    showMatchingErrorDialog(elementData) {
        var dialogContent = this.DialogBuilder.getDialogTemplate(MatchingErrorController, require('../dialog/matching-error/matching-error.tmpl.html'));
        dialogContent.locals = {
            elementData: elementData
        }
        this.$mdDialog.show(dialogContent);
    }

    showCustomizeDialog(elementData, sourceEndpoint, sepa) {
        var dialogContent = this.DialogBuilder.getDialogTemplate(CustomizeController, require('../dialog/customize-pipeline-element/customizeElementDialog.tmpl.html'));
        dialogContent.locals = {
            elementData: elementData,
            sourceEndpoint: sourceEndpoint,
            sepa: sepa
        }
        return this.$mdDialog.show(dialogContent);
    };

    showCustomizeStreamDialog(streamDescription) {
        var dialogContent = this.DialogBuilder.getDialogTemplate(TopicSelectionDialog, require('../dialog/topic/topic-selection-modal.tmpl.html'));
        dialogContent.locals = {
            streamDescription: streamDescription
        }
        this.$mdDialog.show(dialogContent);
    }

    showSavePipelineDialog(pipelineNew, modificationModeOn) {
        var dialogContent = this.DialogBuilder.getDialogTemplate(SavePipelineController, require('../dialog/save-pipeline/submitPipelineModal.tmpl.html'));
        dialogContent.locals = {
            pipeline: pipelineNew,
            modificationMode: modificationModeOn
        }
        this.$mdDialog.show(dialogContent);
    }

    openHelpDialog(pipelineElementPayload) {
        this.$mdDialog.show({
            controller: HelpDialogController,
            controllerAs: 'ctrl',
            template: require('../dialog/help/help-dialog.tmpl.html'),
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
            template: require('../dialog/possible-elements/possible-elements-dialog.tmpl.html'),
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

    showWelcomeDialog(user) {
        this.$mdDialog.show({
            controller: WelcomeTourDialogController,
            controllerAs: 'ctrl',
            template: require('../dialog/welcome-tour/welcome-tour-dialog.tmpl.html'),
            parent: angular.element(document.body),
            clickOutsideToClose: false,
            bindToController: true,
            locals: {
                user: user
            }
        })
    }

    showMissingElementsForTutorialDialog(pipelineElements) {
        this.$mdDialog.show({
            controller: MissingElementsForTutorialDialogController,
            controllerAs: 'ctrl',
            template: require('../dialog/missing-elements-for-tutorial/missing-elements-for-tutorial-dialog.tmpl.html'),
            parent: angular.element(document.body),
            clickOutsideToClose: false,
            bindToController: true,
            locals: {
                pipelineElements: pipelineElements
            }
        })
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

    showMixedStreamAlert(ev) {
        var confirm = this.$mdDialog.confirm()
            .title('Not allowed')
            .textContent('Currently, it is not possible to mix data streams and data sets in a single pipeline.')
            .targetEvent(ev)
            .ok('Ok')
        return this.$mdDialog.show(confirm);
    }
}

EditorDialogManager.$inject = ['$mdDialog', 'DialogBuilder'];