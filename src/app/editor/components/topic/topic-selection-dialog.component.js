import {TopicSelectionDialogController} from "./topic-selection-dialog.controller";

export let TopicSelectionDialogComponent = {
    templateUrl: 'app/editor/components/topic/topic-selection-dialog.tmpl.html',
    bindings: {
        streamDescription: "=",
        titleField: "=",
        topicMappings: "="
    },
    controller: TopicSelectionDialogController,
    controllerAs: 'ctrl'
};
