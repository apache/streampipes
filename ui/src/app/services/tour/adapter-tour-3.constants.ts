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
        id: 'adapter3',
        steps: [
            {
                stepId: 'step-1',
                title: 'Welcome to the Second Adapter Tutorial',
                text:
                    '<p>In this tutorial we connect the live data form parking lots of the LAX airport.</p>' +
                    '<p>Link to data description: https://goo.gl/48y2xv </p>' +
                    'Click <b>next</b> to continue.',
                classes: 'shepherd shepherd-welcome',
                buttons: ['cancel', 'next'],
            },
            {
                stepId: 'step-2',
                title: 'Select a new adapter',
                text:
                    "<p>Let's start!</p>" +
                    '<p>First select the Http Stream adapter</p>',
                attachToElement: '#HTTP_Stream',
                attachPosition: 'bottom',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-3',
                title: 'Configure the protocol',
                text:
                    'The data is provided through a REST endpoint, which has to be polled regularly. To do this set the <b>Interval</b> to <b>60</b>. This configuration will poll the data every minute' +
                    'The <b>URL of the endpoint is: https://data.lacity.org/resource/xzkr-5anj.json</b>, copy this URL into the URL field' +
                    '<p>After setting the configuration, click on next to go to the next step</p>',
                attachToElement: '#formWrapper:last-of-type',
                attachPosition: 'top',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-4',
                title: 'Select the format Json Array No Key',
                text:
                    'Click on <b>Json Array No Key</b> format. The data is provided in an array and no further configurations are required' +
                    'Go to the next step',
                attachToElement: '#json_array_no_key',
                attachPosition: 'left',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-6.0',
                title: 'Change the schema of the data',
                text:
                    'In this view you can see the schema of each data entry. You can use the schema editor to change the schema, add more information and have a look at the data' +
                    '<p>When you understand the schema of the data go to the next step by clicking on the <b>next</b> button!',
                attachToElement: '#schema_reload_button',
                attachPosition: 'top',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-7',
                title: 'Start Adapter',
                text: 'Change the name of the adapter to <b>LAX</b> and click on button <b>Start Adapter</b>',
                attachToElement: '#input-AdapterName',
                attachPosition: 'top',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-8',
                title: 'Adapter was started successfully',
                text: 'Wait for the data! Then finish the creation process by clicking on this button.',
                attachToElement: '#confirm_adapter_started_button',
                attachPosition: 'right',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-9',
                title: 'Congratulation',
                text: 'Congratulation you have created your second adapter and finished the tutorial. Go to the pipeline editor to see the new data source',
                classes: 'shepherd shepherd-welcome',
                buttons: ['cancel'],
            },
        ],
        matchingSteps: [
            { actionId: 'select-adapter', currentStep: 'step-2' },
            {
                actionId: 'specific-settings-next-button',
                currentStep: 'step-3',
            },
            { actionId: 'format-selection-next-button', currentStep: 'step-4' },

            { actionId: 'event-schema-next-button', currentStep: 'step-6.0' },

            { actionId: 'button-startAdapter', currentStep: 'step-7' },
            {
                actionId: 'confirm_adapter_started_button',
                currentStep: 'step-8',
            },
        ],
    },
};
