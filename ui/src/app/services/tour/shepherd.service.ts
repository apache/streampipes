/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import Shepherd from 'shepherd.js';
//import "shepherd.js/dist/css/shepherd-theme-arrows.css";
import {Inject, Injectable} from "@angular/core";

@Injectable()
export class ShepherdService {

    $timeout: any;
    $state: any;
    TourProviderService: any;
    currentTour: any;
    currentTourSettings: any;
    timeWaitMillis: number;

    constructor(@Inject('$timeout') $timeout, @Inject('$state') $state, @Inject('TourProviderService') TourProviderService) {
        this.$timeout = $timeout;
        this.$state = $state;
        this.TourProviderService = TourProviderService;
        this.timeWaitMillis = TourProviderService.getTime();
    }

    makeTour(currentTourSettings) {
        let tour = new Shepherd.Tour({
            defaults: {
                classes: 'shadow-md bg-purple-dark',
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
                 this.$timeout(() => this.currentTour.next(), this.TourProviderService.getTime());
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

    startAdapterTour2() {
        this.startTour(this.TourProviderService.getTourById("adapter2"));
    }

    startAdapterTour3() {
        this.startTour(this.TourProviderService.getTourById("adapter3"));
    }

    setTimeWaitMillies(value) {
        this.TourProviderService.setTime(value);
    }

    getTimeWaitMillies() {
       return this.TourProviderService.getTime();
    }
}

ShepherdService.$inject = ['$timeout', '$state', 'TourProviderService'];