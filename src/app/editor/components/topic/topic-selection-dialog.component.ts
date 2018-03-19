import {TopicSelectionDialogController} from "./topic-selection-dialog.controller";

export let TopicSelectionDialogComponent = {
    templateUrl: 'topic-selection-dialog.tmpl.html',
    bindings: {
        streamDescription: "=",
        titleField: "=",
        topicMappings: "="
    },
    controller: TopicSelectionDialogController,
    controllerAs: 'ctrl'
};
