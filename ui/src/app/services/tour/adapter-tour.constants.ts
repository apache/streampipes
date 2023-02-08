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
                title: 'Welcome to the Adapter Tutorial',
                text: '<p>In this tour you will learn how to create a new adapter, which then can be used as a source in the pipeline editor.</p> Click <b>next</b> to continue.',
                classes: 'shepherd shepherd-welcome',
                buttons: ['cancel', 'next'],
            },
            {
                stepId: 'step-2',
                title: 'Select a new adapter',
                text:
                    "<p>Let's start!</p>" +
                    '<p>This is the <b>OpenSenseMap</b> adapter. OpenSenseMap is an online service where everybody can publish environment data. </p>' +
                    '<div><img src="https://sensebox.de/images/senseBox_home_circle_500.png" alt="Sensebox" height="200" width="200"></div>' +
                    'Click on <b>OpenSenseMap Adapter</b> to continue.',
                attachToElement: '#OpenSenseMap',
                attachPosition: 'bottom',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-3',
                title: 'Select the sensors you are interested in',
                text:
                    '<p><b>Select all sensors</b> in the menu on the left. With the selection you just get the values of the sensor boxes containing all sensors.<p>' +
                    'After selecting all Sensors click on <b>next</b> to continue.',
                attachToElement: '#specific-settings-next-button',
                attachPosition: 'top',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-4',
                title: 'Configure the schema of the data',
                text:
                    'In this editor it is possible to change the schema of the sensebox data. Each entry describes a property. For example the <b>id</b> property contains the unique id of the sensebox. ' +
                    'Open the configuration menu by <b>clicking on the arrow</b>!',
                attachToElement: '#id button:last-of-type',
                attachPosition: 'top',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-5',
                title: 'Change the id runtime name',
                text:
                    'The runtime name represents the key in our Json data objects. Change the value of the runtime name to <b>new_id</b>. This will also change all the keys later in the real data stream. ' +
                    'Then click the <b>next button of the user guide</b>.',
                attachToElement: '#input-runtime-name-Id',
                attachPosition: 'bottom',
                buttons: ['cancel', 'next'],
            },
            {
                stepId: 'step-6',
                title: 'Go to next Step',
                text: 'Finish the modelling and go to next step to start the adapter. Click <b>next</b> to continue.',
                attachToElement: '#event-schema-next-button',
                attachPosition: 'bottom',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-7',
                title: 'Start Adapter',
                text: "Change the name of the adapter to <b>OpenSenseMap</b> and click on <b>button 'Start Adapter'</b>",
                attachToElement: '#input-AdapterName',
                attachPosition: 'top',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-8',
                title: 'Adapter was started successfully',
                text:
                    '<p>The adpater is now deployed in the background and the data is fetched and processed from the OpenSenseMap endpoint. Here you will see some example data, which is coming directly ' +
                    'the data source. This might take a minute. On the left you can see the runtime names of the properties you defined before. On the right side the values are shown. After having a look at ' +
                    'the data <b>click on Close</b> to continue.</p>',
                attachToElement: '#confirm_adapter_started_button',
                attachPosition: 'bottom',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-9',
                title: 'Congratulation',
                text: '<b></b>Congratulation!</b> You have created your first adapter and finished the tutorial. Go to the pipeline editor to see the new data source',
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
            { actionId: 'open-event-property-primitve', currentStep: 'step-4' },
            { actionId: 'event-schema-next-button', currentStep: 'step-6' },
            { actionId: 'button-startAdapter', currentStep: 'step-7' },
            {
                actionId: 'confirm_adapter_started_button',
                currentStep: 'step-8',
            },
        ],
    },
};
