export default {
    adapterTour: {
        id: "adapter",
        steps: [{
            stepId: "step-1",
            title: "Welcome to the Adapter Tutorial",
            text: "<p>In this tour you will learn how to create a new adapter, which then can be used as a source in the pipeline editor.</p> Click <b>next</b> to continue.",
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
                "<p>This is the <b>OpenSenseMap</b> adapter. OpenSenseMap is an online service where everybody can publish environment data. </p>" +
                "<div><img src=\"https://sensebox.de/images/senseBox_home_circle_500.png\" alt=\"Sensebox\" height=\"200\" width=\"200\"></div>",
                attachToElement: "#OpenSenseMap",
                attachPosition: "bottom",
                buttons: [
                    "cancel",
                ]
            },
            {
                stepId: "step-3",
                title: "Select the sensors you are interested in",
                text: "Select all sensors in the menu. With the selection you just get the values of the sensor boxes containing all sensors.",
                attachToElement: "#specific-settings-next-button",
                attachPosition: "top",
                buttons: [
                    "cancel",
                ]
            },
            {
                stepId: "step-4",
                title: "Configure the schema of the data",
                text: "In this editor it is possible to change the schema of the sensebox data. Each entry describes a property. For example the <b>id</b> property contains the unique id of the sensebox. " +
                "Open the configuration menu by clicking on the arrow!",
                attachToElement: "#id button:last-of-type",
                attachPosition: "top",
                buttons: [
                    "cancel",
                ]
            },
            {
                stepId: "step-5",
                title: "Change the id runtime name",
                text: "The runtime name represents the key in our Json data objects. Change the value of the runtime name to 'new_id'. This will also change all the keys later in the real data stream. " +
                "Then click the next button.",
                attachToElement: "#input-runtime-name-Id",
                attachPosition: "bottom",
                buttons: [
                    "cancel",
                    "next"
                ]
            },
             {
                stepId: "step-6",
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
                text: "Change the name of the adapter and click on button 'Start Adapter'",
                attachToElement: "#button-startAdapter",
                attachPosition: "bottom",
                buttons: [
                    "cancel"
                ]
            },
            {
                stepId: "step-8",
                title: "Adapter was started successfully",
                text: "Confirm the start up by clicking on the button",
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
            {actionId: "open-event-property-primitve", currentStep: "step-4"},
            {actionId: "event-schema-next-button", currentStep: "step-6"},
            {actionId: "button-startAdapter", currentStep: "step-7"},
            {actionId: "confirm_adapter_started_button", currentStep: "step-8"},
        ]
    }
}