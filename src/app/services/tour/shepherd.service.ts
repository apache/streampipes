import Shepherd from 'shepherd.js';
import "shepherd.js/dist/css/shepherd-theme-arrows.css";
import {Inject, Injectable} from "@angular/core";

@Injectable()
export class ShepherdService {

    $timeout: any;
    $state: any;
    TourProviderService: any;
    currentTour: any;
    currentTourSettings: any;

    constructor(@Inject('$timeout') $timeout, @Inject('$state') $state, @Inject('TourProviderService') TourProviderService) {
        this.$timeout = $timeout;
        this.$state = $state;
        this.TourProviderService = TourProviderService;
    }

    makeTour(currentTourSettings) {
        let tour = new Shepherd.Tour({
            defaults: {
                classes: 'shepherd-theme-arrows',
                showCancelLink: true
            }
        });

        currentTourSettings.steps.forEach(step => {
            tour.addStep(step.stepId, this.makeStep(tour, step));
        });

        return tour;
    }

    makeStep(tour, step) {
        let stepDefinition = {
            title: step.title,
            text: step.text,
            classes: step.classes,
            buttons: this.generateButtons(tour, step.buttons)
        }

        if (step.attachToElement) {
            Object.assign(stepDefinition, {
                attachTo: {
                    element: step.attachToElement,
                    on: step.attachPosition,
                }
            });
        }

        return stepDefinition;
    }

    generateButtons(tour, buttons) {
        let generatedButtons = [];
        buttons.forEach(button => {
            generatedButtons.push(this.generateButton(tour, button));
        });
        return generatedButtons;
    }

    generateButton(tour, button) {
        if (button === "back") {
            return this.makeBackButton(tour);
        } else if (button === "next") {
            return this.makeNextButton(tour);
        } else if (button === "cancel") {
            return this.makeCancelButton(tour);
        } else if (button === "dashboard") {
            return this.makeStartDashboardTutorialButton();
        }
    }

    startTour(tourSettings) {
        this.currentTourSettings = tourSettings;
        this.currentTour = this.makeTour(this.currentTourSettings);
        this.currentTour.start();
    }


    makeCancelButton(tour) {
        return {
            action: tour.cancel,
            classes: 'shepherd-button-secondary',
            text: 'Exit Tour'
        }
    }

    makeNextButton(tour) {
        return {
            action: tour.next,
            text: 'Next'
        }
    }

    makeBackButton(tour) {
        return {
            action: tour.back,
            classes: 'shepherd-button-secondary',
            text: 'Back'
        };
    }

    makeStartDashboardTutorialButton() {
        return {
            action: this.switchAndStartDashboardTour(),
            text: 'Dashboard Tutorial'
        };
    }

    trigger(actionId) {
        if (Shepherd.activeTour) {
            if (this.shouldTrigger(actionId, this.currentTour.getCurrentStep().id)) {
                this.$timeout(() => this.currentTour.next(), 500);
            }
        }
    }

    shouldTrigger(actionId, currentStep) {
        return this.currentTourSettings.matchingSteps.some(el => {
            return (el.actionId === actionId) && (el.currentStep === currentStep);
        });
    }

    isTourActive() {
        return Shepherd.activeTour !== undefined;
    }

    hideCurrentStep() {
        Shepherd.activeTour.getCurrentStep().hide();
    }

    switchAndStartDashboardTour() {
        this.$state.go("streampipes.dashboard");
    }

    startCreatePipelineTour() {
        this.startTour(this.TourProviderService.getTourById("create-pipeline"));
    }

    startDashboardTour() {
        this.startTour(this.TourProviderService.getTourById("dashboard"));
    }

    startAdapterTour() {
        this.startTour(this.TourProviderService.getTourById("adapter"));
    }
}