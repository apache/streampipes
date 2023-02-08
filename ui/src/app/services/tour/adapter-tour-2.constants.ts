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
        id: 'adapter2',
        steps: [
            {
                stepId: 'step-1',
                title: 'Welcome to the Second Adapter Tutorial',
                text:
                    "<p>In this tutorial you will learn how to get official environment data from the 'Landesamt für Umwelt Baden-Württemberg' LUBW." +
                    '</p> Click <b>next</b> to continue.',
                classes: 'shepherd shepherd-welcome',
                buttons: ['cancel', 'next'],
            },
            {
                stepId: 'step-2',
                title: 'Select a new adapter',
                text:
                    "<p>Let's start!</p>" +
                    '<p>Select the Http Stream adapter to get the data from the LUBW</p>' +
                    '<div><img src="https://www.lubw.baden-wuerttemberg.de/documents/10184/207326/151130_Hauptstaetter_Str_Kleinmessstation_400.jpg/638ab506-0590-47fe-8632-f948e65324b7?t=1478857258000" alt="Sensebox" height="150" width="200"></div>' +
                    'The image shows a measuring station. Those stations are distributed all over Baden-Württemberg',
                attachToElement: '#HTTP_Stream',
                attachPosition: 'bottom',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-3',
                title: 'Configure the protocol',
                text:
                    'The data is provided through a REST endpoint, which has to be polled regularly. To do this set the <b>Interval [Sec]</b> to <b>60</b>. This means the endpoint is accessed every minute.' +
                    'The <b>URL of the endpoint is: restendpoint:3001/lubw</b>, copy this URL into the URL field',
                attachToElement: '#formWrapper:last-of-type',
                attachPosition: 'top',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-4',
                title: 'Select the format geo Json',
                text:
                    'The data is provided in the form of geo Json. This is a Json format optimized for geospatiol data. To process this data select the <b>GeoJson</b> format. This format needs no further configuration.' +
                    ' Go to the next step',
                attachToElement: '#GeoJSON',
                attachPosition: 'right',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-5',
                title: 'Finish Format configuration',
                text: 'After selecting GeoJson as a format go to the next step',
                attachPosition: 'top',
                attachToElement: '#format-selection-next-button',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-6.0',
                title: 'Change the schema of the data',
                text:
                    'The data source of LUWB provices a lot of information and we do not need all of this. Therefore first delete all properties, except the following.' +
                    '<ul>' +
                    '<li><b>no2kont</b> (NO2)</li>' +
                    '<li><b>ozon</b> (Ozon)</li>' +
                    '<li><b>pm10grav</b> (Particulate Matter)</li>' +
                    '<li><b>latitude</b> (Location Latitude)</li>' +
                    '<li><b>longitude</b> (Location Longitude)</li>' +
                    '<li><b>timestamp</b> (Timestemp of data point)</li>' +
                    '<li><b>id</b> (Id of the station)</li>' +
                    '</ul>' +
                    'When you make any errors during the editing you can reset the the schema on the reload button.',
                attachToElement: '#schema_reload_button',
                attachPosition: 'top',
                buttons: ['cancel', 'next'],
            },
            {
                stepId: 'step-6.1',
                title: 'Go to next Step',
                text: 'Finish the modelling and go to next step to start the adapter',
                attachToElement: '#event-schema-next-button',
                attachPosition: 'bottom',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-7',
                title: 'Start Adapter',
                text: 'Change the name of the adapter to <b>LUBW</b> and click on button <b>Start Adapter</b>',
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
                text: 'Congratulation you have created your first adapter and finished the tutorial. Go to the pipeline editor to see the new data source',
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
            { actionId: 'select-format', currentStep: 'step-4' },
            { actionId: 'format-selection-next-button', currentStep: 'step-5' },

            { actionId: 'event-schema-next-button', currentStep: 'step-6.1' },

            { actionId: 'button-startAdapter', currentStep: 'step-7' },
            {
                actionId: 'confirm_adapter_started_button',
                currentStep: 'step-8',
            },
        ],
    },
};
