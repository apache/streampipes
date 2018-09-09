import Shepherd from 'shepherd.js';
import "shepherd.js/dist/css/shepherd-theme-arrows.css";

export class ShepherdService {

    $window: any;
    $timeout: any;
    $state: any;
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

    constructor($window, $timeout, $state) {
        this.$window = $window;
        this.$timeout = $timeout;
        this.$state = $state;
        this.makeTour();

    }

    makeTour() {
        this.tour = new Shepherd.Tour({
            defaults: {
                classes: 'shepherd-theme-arrows',
                showCancelLink: true
            }
        });

        this.tour.addStep('step-1', {
            title: 'Welcome to StreamPipes!',
            text: '<p>This tour will teach you how to create your first pipeline. You will learn how to select streams, how to connect data processors and sinks and how to start a pipeline.</p> Click <b>next</b> to continue.',
            classes: 'shepherd shepherd-welcome',
            buttons: this.makeStandardButtons()
        });

        let step2 = {
            title: 'Pipeline Element Selection',
            text: '<p>Let\' start!</p> <p>This is the <b>Pipeline Element Selection</b> panel. Here you can select pipeline element types. The current panel, data streams, shows a list of currently available streams.</p>',
            attachTo: {
                element: '#pe-type>md-tabs-wrapper>md-tabs-canvas>md-pagination-wrapper md-tab-item:nth-child(2)',
                on: 'bottom'
            },
            buttons: this.makeStandardButtons()
        };

        let step3 = {
            title: 'Selecting data streams',
            text: 'The first element of a pipeline is a data stream, which produces data you\'d like to transform. <p>To select a stream, click the stream named <b>Random Number Stream</b> and drop it in the assembly area below.</p>',
            attachTo: {
                element: '#editor-icon-stand span:last-child>span>pipeline-element',
                on: 'left'
            },
            buttons: this.makeBackButton()
        };

        let step4 = {
            title: 'Creating pipelines',
            text: '<p>Cool!</p> <p>Dragging and dropping elements is the basic principle you need to know to create pipelines. You only need to select a pipeline element and drop it to the assembly area.</p><p>Click <b>Next</b> to continue.</p>',
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
            text: '<p>Now you can see a variety of available data processors.</p><p>Processors can provide simple capabilities such as filters or aggregations, but can also provide more advanced capabilities such as trend and pattern detection or even pre-trained neural networks.</p><p>Select the processor called <b>Field Hasher</b> and move it to the assembly area.</p>',
            attachTo: {
                element: '#editor-icon-stand span:last-child>span>pipeline-element',
                on: 'right'
            },
            buttons: this.makeBackButton()
        };

        let step7 = {
            title: 'Connecting elements',
            text: '<p>Now it\'s time to connect the first elements of your pipeline!</p> <p>Click the gray output port of the stream and connect it with the Field Hasher component.</p>',
            attachTo: {
                element: '#assembly>div.jsplumb-endpoint:nth-of-type(1)',
                on: 'bottom'
            },
            buttons: this.makeBackButton()
        };

        let step8 = {
            title: 'Customize elements',
            text: '<p>Most pipeline elements can be customized according to your needs. Whenever you connect two elements with each other, a configuration dialog pops up.</p><p>Select a <b>Hash Algorithm</b> and a field of the stream you\'d like to hash and click <b>Save</b>.</p>',
            attachTo: {
                element: 'md-dialog>md-dialog-actions>button:last-of-type',
                on: 'top'
            },
            buttons: this.makeBackButton()
        };

        let step9 = {
            title: 'Selecting data sinks',
            text: '<p>What\'s missing?</p><p>Every pipeline needs a data sink. Sinks define what to do with the output of your pipeline and can be visualizations, notifications, or can trigger third party components or even actuators.</p><p>Click the tab above to see available data sinks.</p>',
            attachTo: {
                element: '#pe-type>md-tabs-wrapper>md-tabs-canvas>md-pagination-wrapper md-tab-item:nth-child(4)',
                on: 'bottom'
            },
            buttons: this.makeBackButton()
        };

        let step10 = {
            title: 'Finish Pipeline',
            text: '<p>Almost there!</p>Select the <b>Dashboard</b> sink and connect the <b>Field Hasher</b> to the Dashboard.</p>',
            attachTo: {
                element: '#editor-icon-stand span:last-of-type>span>pipeline-element',
                on: 'left'
            },
            buttons: this.makeBackButton()
        };

        let step11 = {
            title: 'Save Pipeline',
            text: '<p>Great!</p><p>Your first pipeline is complete. No we\'re ready to start the pipeline.</p><p>Click the <b>Save</b> icon to open the save dialog.</p>',
            attachTo: {
                element: '.assemblyOptions>div>button:nth-of-type(1)',
                on: 'left'
            },
            buttons: this.makeBackButton()
        };

        let step12 = {
            title: 'Save Pipeline Dialog',
            text: '<p>Enter a name and an optional description of your pipeline.</p><p>Afterwards, make sure that <b>Start pipeline immediately</b> is checked.</p><p>Click on <b>Save and go to pipeline view</b> to start the pipeline.</p>',
            attachTo: {
                element: 'md-dialog>md-dialog-actions>button:nth-of-type(3)',
                on: 'bottom'
            },
            buttons: this.makeBackButton()
        };

        let step13 = {
            title: 'Pipeline Started',
            text: '<p>Congratulations!</p><p>You\'ve completed the first tutorial. The next step would be to add a visualization in the <b>Dashboard</b>. If you wish to see how it works, start the second tutorial.</p>',
            attachTo: {
                element: 'md-dialog>form>md-dialog-actions>button:nth-of-type(1)',
                on: 'bottom'
            },
            buttons: this.makeNextTutorialButton()
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
                action: this.tour.cancel,
                classes: 'shepherd-button-secondary',
                text: 'Exit Tour'
            }, {
                action: this.tour.next,
                text: 'Next'
            }
        ]
    }

    makeBackButton() {
        return [
            {
                action: this.tour.cancel,
                classes: 'shepherd-button-secondary',
                text: 'Exit Tour'
            }
        ]
    }

    makeNextTutorialButton() {
        return [
            {
                action: this.tour.cancel,
                classes: 'shepherd-button-secondary',
                text: 'Exit Tour'
            }, {
                action: this.startDashboardTutorial,
                text: 'Dashboard Tutorial'
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
        Shepherd.activeTour.getCurrentStep().hide();
    }

    startDashboardTutorial() {

    }
}