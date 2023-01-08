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
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { TourProviderService } from './tour-provider.service';
import Step from 'shepherd.js/src/types/step';
import StepOptions = Step.StepOptions;

@Injectable()
export class ShepherdService {
    currentTour: any;
    currentTourSettings: any;
    timeWaitMillis: number;

    constructor(
        private router: Router,
        private tourProviderService: TourProviderService,
    ) {
        this.timeWaitMillis = tourProviderService.getTime();
    }

    makeTour(currentTourSettings) {
        const tour = new Shepherd.Tour({
            confirmCancel: true,
            confirmCancelMessage: 'Do you really want to cancel the tour?',
            defaultStepOptions: {
                classes: 'shadow-md bg-purple-dark',
                scrollTo: true,
                // showCancelLink: true
            },
        });

        currentTourSettings.steps.forEach(step => {
            tour.addStep(this.makeStep(tour, step));
        });

        return tour;
    }

    makeStep(tour, step) {
        const stepDefinition: StepOptions = {
            title: step.title,
            text: step.text,
            classes: step.classes,
            id: step.stepId,
            buttons: this.generateButtons(tour, step.buttons),
        };

        if (step.attachToElement) {
            Object.assign(stepDefinition, {
                attachTo: {
                    element: step.attachToElement,
                    on: step.attachPosition,
                },
            });
        }

        return stepDefinition;
    }

    generateButtons(tour, buttons) {
        const generatedButtons = [];
        buttons.forEach(button => {
            generatedButtons.push(this.generateButton(tour, button));
        });
        return generatedButtons;
    }

    generateButton(tour, button) {
        if (button === 'back') {
            return this.makeBackButton(tour);
        } else if (button === 'next') {
            return this.makeNextButton(tour);
        } else if (button === 'cancel') {
            return this.makeCancelButton(tour);
        } else if (button === 'dashboard') {
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
            text: 'Exit Tour',
        };
    }

    makeNextButton(tour) {
        return {
            action: tour.next,
            text: 'Next',
        };
    }

    makeBackButton(tour) {
        return {
            action: tour.back,
            classes: 'shepherd-button-secondary',
            text: 'Back',
        };
    }

    makeStartDashboardTutorialButton() {
        return {
            action: this.switchAndStartDashboardTour(),
            text: 'Dashboard Tutorial',
        };
    }

    trigger(actionId) {
        if (Shepherd.activeTour) {
            if (
                this.shouldTrigger(
                    actionId,
                    this.currentTour.getCurrentStep().id,
                )
            ) {
                setTimeout(
                    () => this.currentTour.next(),
                    this.tourProviderService.getTime(),
                );
            }
        }
    }

    shouldTrigger(actionId, currentStep) {
        return this.currentTourSettings.matchingSteps.some(el => {
            return el.actionId === actionId && el.currentStep === currentStep;
        });
    }

    isTourActive() {
        return Shepherd.activeTour;
    }

    hideCurrentStep() {
        Shepherd.activeTour.getCurrentStep().hide();
    }

    switchAndStartDashboardTour() {
        this.router.navigateByUrl('dashboard');
    }

    startCreatePipelineTour() {
        this.startTour(this.tourProviderService.getTourById('create-pipeline'));
    }

    startDashboardTour() {
        this.startTour(this.tourProviderService.getTourById('dashboard'));
    }

    startAdapterTour() {
        this.startTour(this.tourProviderService.getTourById('adapter'));
    }

    startAdapterTour2() {
        this.startTour(this.tourProviderService.getTourById('adapter2'));
    }

    startAdapterTour3() {
        this.startTour(this.tourProviderService.getTourById('adapter3'));
    }

    setTimeWaitMillies(value) {
        this.tourProviderService.setTime(value);
    }

    getTimeWaitMillies() {
        return this.tourProviderService.getTime();
    }
}
