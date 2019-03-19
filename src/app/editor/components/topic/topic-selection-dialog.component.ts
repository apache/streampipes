import {TopicSelectionDialogController} from "./topic-selection-dialog.controller";
declare const require: any;

export let TopicSelectionDialogComponent = {
    template: require('./topic-selection-dialog.tmpl.html'),
    bindings: {
        streamDescription: "=",
        titleField: "=",
        topicMappings: "="
    },
    controller: TopicSelectionDialogController,
    controllerAs: 'ctrl'
};
