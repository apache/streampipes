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
    dashboardTour: {
        id: 'dashboard',
        steps: [
            {
                stepId: 'step-1',
                title: 'Welcome to the Dashboard Tutorial!',
                classes: 'shepherd shepherd-welcome',
                text: "<p>In this tutorial, you'll learn how to create a live dashboard that shows data from a running pipeline.</p><b>Note:</b>This tutorial requires a running pipeline that makes use of the <i>Dashboard Sink</i>. If you don't have such a pipeine started yet, go to the pipeline editor and create one.<p>Press <i>Next</i> to start the tutorial!</p>",
                buttons: ['cancel', 'next'],
            },
            {
                stepId: 'step-2',
                title: 'Add visualization',
                text: "<p>Let's start!</p><p>To create a new visulization, click the <i>Add visualization</i> button.</p>",
                attachToElement: '#add-viz-button',
                attachPosition: 'bottom',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-3',
                title: 'Select pipeline',
                text: '<p>This wizard allows to configure visualizations. The first step is to select a currently running pipeline.</p><p>Click the pipeline shown on the left.</p>',
                attachToElement: 'md-grid-tile:nth-of-type(1)',
                attachPosition: 'right',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-4',
                title: 'Select visualization',
                text: "<p>The second step requires the selection of a visulization. You can select from tables, gauges, maps or charts.</p><p>In this tutorial, we'll create a table visualization, so click the <i>Table</i> visualization on the left.</p>",
                attachToElement: 'md-grid-tile:nth-of-type(1)',
                attachPosition: 'right',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-5',
                title: 'Customize visualization',
                text: '<p>Some visualizations can be customized according to your needs.</p><p>For instance, the table visualization provides a dialog to filter columns. Click the <i>Select all</i> button on the left to select all fields from the incoming event.</p>',
                attachToElement: '#select-all-button',
                attachPosition: 'right',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-6',
                title: 'Save visualization',
                text: 'Now you are ready to save your visualization. Click <i>Save</i> to create a new visualization in your dashboard.',
                attachToElement: '#save-viz-button',
                attachPosition: 'bottom',
                buttons: ['cancel'],
            },
            {
                stepId: 'step-7',
                title: 'Cool!',
                text: '<p>Now you should see live data coming in. There are many more visualization types you can try.</p>Press <i>Exit</i> to finish the tour.',
                attachToElement: '.widget-container:nth-of-type(1)',
                attachPosition: 'right',
                buttons: ['cancel'],
            },
        ],
        matchingSteps: [
            { actionId: 'add-viz', currentStep: 'step-2' },
            { actionId: 'select-pipeline', currentStep: 'step-3' },
            { actionId: 'select-viz', currentStep: 'step-4' },
            { actionId: 'customize-viz', currentStep: 'step-5' },
            { actionId: 'save-viz', currentStep: 'step-6' },
        ],
    },
};
