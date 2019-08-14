import {FeedbackController} from "./feedback.controller";

declare const require: any;

export let FeedbackComponent = {
    template: require('./feedback.tmpl.html'),
    bindings: {
        closeFeedbackWindow: "&",
    },
    controller: FeedbackController,
    controllerAs: 'ctrl'
};
