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
                text: '<p>This is the <b>Connect</b> view, where you create adapters for data streams. Adapters create data streams, and those streams can be used in pipelines or visualizations.</p><p>Click <b>Next</b> to continue.</p>',
                classes: 'shepherd shepherd-welcome',
                buttons: ['cancel', 'next'],
            },
            {
                stepId: 'step-3',
                title: 'New adapter',
                text: '<p>Click <b>New adapter</b> to open the list of available adapters.</p>',
                attachToElement:
                    '[data-cy="connect-create-new-adapter-button"]',
                attachPosition: 'left',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-4',
                title: 'Select the machine data simulator',
                text: "<p>The adapter overview lists all installed adapters, including a simulator we'll use for this tutorial.</p><p>Search for and <b>click</b> <b>Machine Data Simulator</b>.</p>",
                attachToElement: '#Machine_Data_Simulator',
                attachPosition: 'top',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-5',
                title: 'Adapter settings',
                text: 'Adapter creation follows a four-step process. In this first step, you can provide basic settings like protocol info. For this tutorial, keep the defaults to produce <b>Flow Rate</b> measurements at one message per second.',
                attachToElement: '[data-cy="adapter-settings-next-button"]',
                attachPosition: 'top',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-6',
                title: 'Configure schema',
                text: '<p>In this step, you can preview the incoming message and optionally enable a transformation script to modify the event.</p><p>Enable the script to continue.</p>',
                attachToElement: '[data-cy="toggle-script-active"]',
                attachPosition: 'top',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-8',
                title: 'Rename and run the script',
                text: '<p>Field renaming is now done in the transformation script. Update the script to rename the <b>temperature</b> field to <b>temp</b>.</p><p>This is how the resulting script should look:</p><pre><code>function transform(event, out, ctx) {\n  event.temp = event.temperature;\n  delete event.temperature;\n  out.collect(event);\n}</code></pre><p>Click <b>Run script</b> to apply the transformation and update the preview.</p>',
                attachToElement: '[data-cy="configure-schema-script-editor"]',
                attachPosition: 'top',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-9',
                title: 'Continue',
                text: 'Click <b>Next</b> to continue.',
                attachToElement: '[data-cy="configure-schema-next-button"]',
                attachPosition: 'bottom',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-10',
                title: 'Configure fields',
                text: 'Review and adjust the detected fields. Use the scope dropdown to mark the timestamp field as <b>Timestamp</b>.',
                attachToElement: '[data-cy="event-property-row"]',
                attachPosition: 'top',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-11',
                title: 'Message preview',
                text: 'Use the preview to verify the resulting event structure after your changes.',
                attachToElement:
                    '[data-cy="configure-fields-event-preview-result"]',
                attachPosition: 'bottom',
                buttons: ['cancel', 'next'],
            },
            {
                stepId: 'step-12',
                title: 'Go to next step',
                text: 'Finish configuring fields and continue to the final step to start the adapter. Click <b>Next</b> to continue.',
                attachToElement: '[data-cy="configure-fields-next-button"]',
                attachPosition: 'bottom',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-13',
                title: 'Adapter name',
                text: 'Change the name of the adapter to <b>Tutorial</b> and click outside the input field to continue.',
                attachToElement: '[data-cy="sp-adapter-name"]',
                attachPosition: 'top',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-14',
                title: 'Persistence',
                text: 'Check <b>Persist</b> to store all messages produced by this adapter. Only persisted data can be visualized in the dashboard or inspected in the data lake.<br/>You can also persist data later using the pipeline editor.',
                attachToElement: '[data-cy="sp-store-in-datalake"]',
                attachPosition: 'top',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-15',
                title: 'Start Adapter',
                text: "Now it's time to start the adapter. Click <b>Start adapter</b> to deploy it.",
                attachToElement:
                    '[data-cy="adapter-settings-start-adapter-btn"]',
                attachPosition: 'top',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-16',
                title: 'Congratulations',
                text: '<p><b>Congratulations!</b> You have created your first adapter and finished the tutorial.</p><p>Go to the pipeline editor to see the new data stream.</p>',
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
            {
                actionId: 'configure-schema-script-enabled',
                currentStep: 'step-6',
            },
            {
                actionId: 'configure-schema-script-run',
                currentStep: 'step-8',
            },
            {
                actionId: 'configure-schema-next-button',
                currentStep: 'step-9',
            },
            {
                actionId: 'timestamp-property-selected',
                currentStep: 'step-10',
            },
            { actionId: 'event-schema-next-button', currentStep: 'step-12' },
            { actionId: 'adapter-name-assigned', currentStep: 'step-13' },
            { actionId: 'adapter-persist-selected', currentStep: 'step-14' },
            {
                actionId: 'adapter-settings-adapter-started',
                currentStep: 'step-15',
            },
        ],
    },
};
