import Shepherd from 'shepherd.js';
import "shepherd.js/dist/css/shepherd-theme-arrows.css";

export class ShepherdService {

    $window: any;
    $timeout: any;
    tour: any;

    matchingSteps:any = [
        {actionId: "drop-stream", currentStep: "step-3"},
        {actionId: "select-sepa", currentStep: "step-5"},
        {actionId: "drop-sepa", currentStep: "step-6"},
        {actionId: "customize-sepa", currentStep: "step-7"},
        {actionId: "save-sepa", currentStep: "step-8"},
        {actionId: "select-action", currentStep: "step-9"},
        {actionId: "customize-action", currentStep: "step-10"},
        {actionId: "save-pipeline-dialog", currentStep: "step-11"},
        {actionId: "pipeline-started", currentStep: "step-12"},
    ];

    constructor($window, $timeout) {
        this.$window = $window;
        this.$timeout = $timeout;
        this.makeTour();

    }

    makeTour() {
        this.tour = new Shepherd.Tour({
            defaults: {
                classes: 'shepherd-theme-arrows',
                showCancelLink: true
            }
        });

        // let step0 = {
        //     title: 'Selecting data streams',
        //     text: 'To select a stream, click the stream marked above and move it to the assembly area.',
        //     attachTo: {
        //         element: '#editor-icon-stand span:nth-child(3)>span>pipeline-element',
        //         on: 'left'
        //     },
        //     buttons: this.makeBackButton()
        // };
        // this.tour.addStep('step-0', step0);

        this.tour.addStep('step-1', {
            title: 'Welcome to StreamPipes!',
            text: 'This tour will teach you how to create your first pipeline. Click <i>next</i> to continue.',
            classes: 'shepherd shepherd-welcome',
            buttons: this.makeStandardButtons()
        });

        let step2 = {
            title: 'Pipeline Element Selection',
            text: 'Here you can select pipeline element types. The current panel, data streams, shows a list of currently available streams.<br/>Select <i>Data Processor</i> to see currently available processors.',
            attachTo: {
                element: '#pe-type>md-tabs-wrapper>md-tabs-canvas>md-pagination-wrapper md-tab-item:nth-child(2)',
                on: 'bottom'
            },
            buttons: this.makeStandardButtons()
        };

        let step3 = {
            title: 'Selecting data streams',
            text: 'To select a stream, click the stream marked above and move it to the assembly area.',
            attachTo: {
                element: '#editor-icon-stand span:nth-child(2)>span>pipeline-element',
                on: 'left'
            },
            buttons: this.makeBackButton()
        };

        let step4 = {
            title: 'Creating pipelines',
            text: 'Cool!<br/> That is how you create pipelines. Select a pipeline element and drop it to the assembly area.',
            attachTo: {
                element: '#assembly>pipeline>div>span:nth-child(1)',
                on: 'left'
            },
            buttons: this.makeStandardButtons()
        };

        let step5 = {
            title: 'Switching between pipeline element types',
            text: 'No we will add a data processor. Select the data processor tab to see a list of currently available processors.',
            attachTo: {
                element: '#pe-type>md-tabs-wrapper>md-tabs-canvas>md-pagination-wrapper md-tab-item:nth-child(3)',
                on: 'bottom'
            },
            buttons: this.makeBackButton()
        };

        let step6 = {
            title: 'Selecting data processors',
            text: '</p>',
            attachTo: {
                element: '#editor-icon-stand span:nth-child(2)>span>pipeline-element',
                on: 'left'
            },
            buttons: this.makeBackButton()
        };

        let step7 = {
            title: 'Connecting elements',
            text: 'Now connect elements.</p>',
            attachTo: {
                element: '#assembly>div.jsplumb-endpoint:nth-of-type(1)',
                on: 'bottom'
            },
            buttons: this.makeBackButton()
        };

        let step8 = {
            title: 'Customize element',
            text: 'Now customize elements.</p>',
            attachTo: {
                element: 'md-dialog>md-toolbar',
                on: 'left'
            },
            buttons: this.makeBackButton()
        };

        let step9 = {
            title: 'Selecting data sinks',
            text: 'Now select a sink.</p>',
            attachTo: {
                element: '#pe-type>md-tabs-wrapper>md-tabs-canvas>md-pagination-wrapper md-tab-item:nth-child(4)',
                on: 'bottom'
            },
            buttons: this.makeBackButton()
        };

        let step10 = {
            title: 'Finish Pipeline',
            text: 'Select and connect last element.</p>',
            attachTo: {
                element: '#editor-icon-stand span:nth-child(2)>span>pipeline-element',
                on: 'left'
            },
            buttons: this.makeBackButton()
        };

        let step11 = {
            title: 'Save Pipeline',
            text: 'Click to save</p>',
            attachTo: {
                element: '.assemblyOptions>div>button:nth-of-type(1)',
                on: 'left'
            },
            buttons: this.makeBackButton()
        };

        let step12 = {
            title: 'Save Pipeline Dialog',
            text: 'Click to save</p>',
            attachTo: {
                element: 'md-dialog>div.md-actions>button:nth-of-type(3)',
                on: 'right'
            },
            buttons: this.makeBackButton()
        };

        let step13 = {
            title: 'Pipeline Started',
            text: 'Congratulations!</p>',
            attachTo: {
                element: 'md-dialog>form>md-toolbar',
                on: 'bottom'
            },
            buttons: this.makeBackButton()
        };

        this.tour.addStep('step-2', step2);
        this.tour.addStep('step-3', step3);
        this.tour.addStep('step-4', step4);
        this.tour.addStep('step-5', step5);
        this.tour.addStep('step-6', step6);
        this.tour.addStep('step-7', step7);
        this.tour.addStep('step-8', step8);
        this.tour.addStep('step-9', step9);
        this.tour.addStep('step-10', step10);
        this.tour.addStep('step-11', step11);
        this.tour.addStep('step-12', step12);
        this.tour.addStep('step-13', step13);
    }

    startTour() {
        this.tour.start();
    }

    makeStandardButtons() {
        return [
            {
                action: this.tour.back,
                classes: 'shepherd-button-secondary',
                text: 'Back'
            }, {
                action: this.tour.next,
                text: 'Next'
            }
        ]
    }

    makeBackButton() {
        return [
            {
                action: this.tour.back,
                classes: 'shepherd-button-secondary',
                text: 'Back'
            }
        ]
    }

    trigger(actionId) {
        if (Shepherd.activeTour) {
            if (this.shouldTrigger(actionId, this.tour.getCurrentStep().id)) {
                this.$timeout(() => this.tour.next(), 500);
            }
        }
    }

    shouldTrigger(actionId, currentStep) {
        return this.matchingSteps.some(el => {
           return (el.actionId === actionId) && (el.currentStep === currentStep);
        });
    }

    isTourActive() {
        return Shepherd.activeTour !== undefined;
    }

    hideCurrentStep() {
        Shepherd.activeTour.getCurrentStep().cancel();
    }

}