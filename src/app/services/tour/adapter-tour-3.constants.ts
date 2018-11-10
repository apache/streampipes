export default {
    adapterTour: {
        id: "adapter2",
        steps: [{
            stepId: "step-1",
            title: "Welcome to the Second Adapter Tutorial",
            text: "<p>In this tutorial we connect the live data form tparking lots of the LAX airport: https://goo.gl/48y2xv" +
            "</p> Click <b>next</b> to continue.",
            classes: "shepherd shepherd-welcome",
            buttons: [
                "cancel",
                "next"
            ]
        },
            {
                stepId: "step-2",
                title: "Select a new adapter",
                text: "<p>Let's start!</p>" +
                "<p>First select the Http Stream adapter</p>",
                attachToElement: "#HTTP_Stream",
                attachPosition: "bottom",
                buttons: [
                    "cancel",
                ]
            },
            {
                stepId: "step-3",
                title: "Configure the protocol",
                text: "The data is provided through a REST endpoint, which has to be polled regularly. To do this set the <b>Interval</b> to <b>60</b>. This configuration will poll the data every minute" +
                "The <b>URL of the endpoint is: https://data.lacity.org/resource/xzkr-5anj.json</b>, copy this URL into the URL field",
                attachToElement: "#formWrapper:last-of-type",
                attachPosition: "top",
                buttons: [
                    "cancel",
                ]
            },
            {
                stepId: "step-4",
                title: "Select the format Json Array No Key",
                text: "Click on <b>Json Array No Key</b> format. The data is provided in an array and no further configurations are required" +
                "Go to the next step",
                attachToElement: "#JsonArrayNoKey",
                attachPosition: "left",
                buttons: [
                    "cancel",
                ]
            },
            {
                stepId: "step-5",
                title: "Finish Format configuration",
                text: "After selecting Json Array No Key as a format go to the next step",
                attachPosition: "top",
                attachToElement: "#format-selection-next-button",
                buttons: [
                    "cancel",
                ]
            },
            {
                stepId: "step-6.0",
                title: "Change the schema of the data",
                text: "In this view you can see the schema of each data entry. You can use the schema editor to change the schema, add more information and have a look at the data",
                attachToElement: "#schema_reload_button",
                attachPosition: "top",
                buttons: [
                    "cancel",
                    "next"

                ]
            },
            {
                stepId: "step-6.1",
                title: "Go to next Step",
                text: "Finish the modelling and go to next step to start the adapter",
                attachToElement: "#event-schema-next-button",
                attachPosition: "bottom",
                buttons: [
                    "cancel"
                ]
            },
            {
                stepId: "step-7",
                title: "Start Adapter",
                text: "Change the name of the adapter to <b>LAX</b> and click on button <b>Start Adapter</b>",
                attachToElement: "#input-AdapterName",
                attachPosition: "top",
                buttons: [
                    "cancel"
                ]
            },
            {
                stepId: "step-8",
                title: "Adapter was started successfully",
                text: "Wait for the data! Then finish the creation process by clicking on this button.",
                attachToElement: "#confirm_adapter_started_button",
                attachPosition: "right",
                buttons: [
                    "cancel"
                ]
            },
            {
                stepId: "step-9",
                title: "Congratulation",
                text: "Congratulation you have created your first adapter and finished the tutorial. Go to the pipeline editor to see the new data source",
                classes: "shepherd shepherd-welcome",
                buttons: [
                    "cancel"
                ]
            }
        ],
        matchingSteps: [
            {actionId: "select-adapter", currentStep: "step-2"},
            {actionId: "specific-settings-next-button", currentStep: "step-3"},
            {actionId: "select-geojson", currentStep: "step-4"},
            {actionId: "format-selection-next-button", currentStep: "step-5"},

            {actionId: "event-schema-next-button", currentStep: "step-6.1"},

            {actionId: "button-startAdapter", currentStep: "step-7"},
            {actionId: "confirm_adapter_started_button", currentStep: "step-8"},
        ]
    }
}