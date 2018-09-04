import Shepherd from 'shepherd.js';
import "shepherd.js/dist/css/shepherd-theme-arrows.css";

export class ShepherdService {

    $window: any;
    tour: any;

    matchingSteps:any = [{actionId: "sepa", currentStep: "step-2"},
        {actionId: "stream", currentStep: "step-3"},
    ];

    constructor($window) {
        console.log("new tour");
        this.$window = $window;
        this.makeTour();

    }

    makeTour() {
        this.tour = new Shepherd.Tour({
            defaults: {
                classes: 'shepherd-theme-arrows'
            }
        });

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
            buttons: this.makeBackButton()
        };

        let step3 = {
            title: 'Available Pipeline Elements',
            text: 'Great!<br/> Now you can see the currently available data processors. Data Processors allow you to transform data streams with arbitrary processing logic.<p>You have just learned how to navigate in the pipeline editor. Switch back to <i>Data Streams</i>to start creating your first pipeline.</p>',
            attachTo: {
                element: '#editor-icon-stand',
                on: 'bottom'
            },
            buttons: this.makeBackButton()
        };

        let step4 = {
            title: 'Selecting data streams',
            text: 'To select a stream, click the stream marked above and move it to the assembly area.',
            attachTo: {
                element: '#editor-icon-stand span:nth-child(3)>span>pipeline-element',
                on: 'bottom'
            },
            buttons: this.makeBackButton()
        };

        this.tour.addStep('step-2', step2);
        this.tour.addStep('step-3', step3);
        this.tour.addStep('step-4', step4);
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
        console.log(actionId);
        if (Shepherd.activeTour) {
            if (this.shouldTrigger(actionId, this.tour.getCurrentStep().id)) {
                this.tour.next();
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

}