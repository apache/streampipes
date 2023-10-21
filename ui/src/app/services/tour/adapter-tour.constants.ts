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

export default {
    adapterTour: {
        id: 'adapter',
        steps: [
            {
                stepId: 'step-1',
                title: 'Welcome to Connect!',
                text: "<p>This is the <b>Connect</b> view. It's the place where adapters for data streams can be created. Adapters create data streams, and data streams can be used anywhere to create pipelines or to visualize data. </p> Click <b>next</b> to continue.",
                classes: 'shepherd shepherd-welcome',
                buttons: ['cancel', 'next'],
            },
            {
                stepId: 'step-2',
                title: 'Existing adapters',
                text: "<p>In this view, you see an overview of existing adapters. If you're just getting started, the view will be empty. Don't worry - we'll now create our first adapter!</p> Click <b>next</b> to continue.",
                attachPosition: 'bottom',
                buttons: ['cancel', 'next'],
            },
            {
                stepId: 'step-3',
                title: 'New adapter',
                text: '<p>Click on <b>New adapter</b> to see a list of all available adapters you can configure.',
                attachToElement:
                    '[data-cy="connect-create-new-adapter-button"]',
                attachPosition: 'left',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-4',
                title: 'Select the machine data simulator',
                text: "<p>All installed adapters are available in the adapter overview. <br/>Despite many industrial protocols, you'll also find a simulator which we'll use to show how to create adapters.<br/>Search for and <b>click</b> the machine data simulator.</p>",
                attachToElement: '#Machine_Data_Simulator',
                attachPosition: 'top',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-5',
                title: 'Adapter settings',
                text: "Adapter creation follows a three-step process. In the first step, you can provide basic settings, such as protocol infos and other settings. For this example, we won't change the default values and will produce a stream which produces <b>Flow Rate</b> measurements at an interval of one message per second.",
                attachToElement: '[data-cy="adapter-settings-next-button"]',
                attachPosition: 'top',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-6',
                title: 'Schema and field configuration',
                text: '<p>In the schema editor, a preview of the message is shown. It is also possible to modify incoming data in form of pre-processing rules. Click <b>Next</b> to continue.',
                buttons: ['cancel', 'next'],
            },
            {
                stepId: 'step-7',
                title: 'Schema and field configuration',
                text: '<p>To modify an existing field, click the <b>Edit Field</b> button. Do this for the <b>Temperature</b> field.',
                attachToElement: '[data-cy="edit-temperature"]',
                attachPosition: 'right',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-8',
                title: 'Change runtime name',
                text: 'The runtime name represents the field name of the message. Change the value of the runtime name to <b>temp</b>. This will also change all the keys later in the data stream. Click outside the input field to continue.',
                attachToElement: '[data-cy="connect-edit-field-runtime-name"]',
                attachPosition: 'top',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-9',
                title: 'Save changes',
                text: 'Click <b>Save</b> to store changes to a field.',
                attachToElement: '[data-cy="sp-save-edit-property"]',
                attachPosition: 'bottom',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-10',
                title: 'Message preview',
                text: 'In the preview, you can see the transformed message, as it will appear in the resulting data stream. You can compare changes to the original. Click <b>Next</b> to continue.',
                attachToElement: '[data-cy="connect-schema-update-preview"]',
                attachPosition: 'bottom',
                buttons: ['cancel', 'next'],
            },
            {
                stepId: 'step-11',
                title: 'Go to next step',
                text: 'Finish the modelling and go to next step to start the adapter. Click <b>next</b> to continue.',
                attachToElement: '[data-cy="sp-event-schema-next-button"]',
                attachPosition: 'bottom',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-12',
                title: 'Adapter name',
                text: 'Change the name of the adapter to <b>Tutorial</b> and click outside the input field to continue.',
                attachToElement: '[data-cy="sp-adapter-name"]',
                attachPosition: 'top',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-13',
                title: 'Persistence',
                text: 'Check the <b>Persist</b> option to persist all messages that are produced by this adapter. Only persisted data can be visualized in the dashboard or inspected in the data lake. <br>You can also persist data later by using the pipeline editor. ',
                attachToElement: '[data-cy="sp-store-in-datalake"]',
                attachPosition: 'top',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-14',
                title: 'Start Adapter',
                text: "Now it's time to start our adapter! Click <b>Start adapter</b> to deploy the adapter.",
                attachToElement:
                    '[data-cy="adapter-settings-start-adapter-btn"]',
                attachPosition: 'top',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-15',
                title: 'Congratulations',
                text: '<b></b>Congratulations!</b> You have created your first adapter and finished the tutorial. Go to the pipeline editor to see the new data stream',
                classes: 'shepherd shepherd-welcome',
                buttons: ['cancel', 'pipeline-tutorial'],
            },
        ],
        matchingSteps: [
            { actionId: 'new-adapter-clicked', currentStep: 'step-3' },
            { actionId: 'new-adapter-selected', currentStep: 'step-4' },
            {
                actionId: 'specific-settings-next-button',
                currentStep: 'step-5',
            },
            { actionId: 'adapter-edit-field-clicked', currentStep: 'step-7' },
            { actionId: 'adapter-runtime-name-changed', currentStep: 'step-8' },
            { actionId: 'adapter-field-changed', currentStep: 'step-9' },
            { actionId: 'event-schema-next-button', currentStep: 'step-11' },
            { actionId: 'adapter-name-assigned', currentStep: 'step-12' },
            { actionId: 'adapter-persist-selected', currentStep: 'step-13' },
            {
                actionId: 'adapter-settings-adapter-started',
                currentStep: 'step-14',
            },
        ],
    },
};
