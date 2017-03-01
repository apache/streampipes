TutorialCtrl.$inject = ['$scope', '$timeout'];

export default function TutorialCtrl($scope, $timeout) {

    var makeTutorialPage = function(title, description, imageUrl) {
        return {"title" : title, "description" : description, "image" : imageUrl};
    }

    $scope.tutorialPages = [];

    $scope.tutorialPages.push(makeTutorialPage("Welcome!", "This tutorial will give you a quick introduction to StreamPipes and will teach you how to create your first pipeline.", "img/tut2/1.png"));
    $scope.tutorialPages.push(makeTutorialPage("StreamPipes", "StreamPipes helps you to integrate and transform real-time data streams without writing any line of code by providing a simple web-based user interface.", "img/tut2/2.png"));
    $scope.tutorialPages.push(makeTutorialPage("Navigation Bar", "The left navigation bar lets you explore the StreamPipes framework. You can create, start and visualize pipelines. Open the menu by clicking the 'Sandwich' icon in the top left corner of the screen.", "img/tut2/3.png"));
    $scope.tutorialPages.push(makeTutorialPage("Pipeline Editor", "The pipeline editor allows to define processing pipelines in a flexible way. Open the editor by selecting the 'Pipeline Editor' view in the left navigation bar.", "img/tut2/4.png"));
    $scope.tutorialPages.push(makeTutorialPage("Data Streams", "Select a data stream and drag it into the assembly area.", "img/tut2/5.png"));
    $scope.tutorialPages.push(makeTutorialPage("Processing Elements", "Switch to the 'Processing Elements' tab below the upper navigation bar, select the 'Aggregation' element and drag it into the assembly as well.", "img/tut2/6.png"));
    $scope.tutorialPages.push(makeTutorialPage("Connecting Elements", "Connect both elements by drawing a line from the data stream to the processing element using the grey circles. A configuration dialog pops up allowing you to customize the aggregation. Select an aggregation type, leave the 'group by' field untouched, select a value you'd like to aggregate and provide values for output frequency and time window. Click 'Save' to continue.", "img/tut2/7.png"));
    $scope.tutorialPages.push(makeTutorialPage("Data Sinks", "Switch to the 'Data Sinks' tab, select the 'Dashboard (DS)' component, drag it into the assembly and connect it with the 'Aggregation' element.", "img/tut2/8.png"));
    $scope.tutorialPages.push(makeTutorialPage("Creating Pipelines", "Click the 'Floppy Disk' icon in the options panel of the assembly. In the following dialog, provide a name for your first pipeline, select 'Start Pipeline Immediately' and click 'Save and switch to pipeline view.'", "img/tut2/9.png"));
    $scope.tutorialPages.push(makeTutorialPage("Starting Pipelines", "The system redirects you to the pipeline view and starts the pipeline. The pipeline view allows you to start and stop pipelines on demand at any time.", "img/tut2/10.png"));
    $scope.tutorialPages.push(makeTutorialPage("Dashboard", "Open the left navigation bar and switch to the 'Dashboard' view. Click 'Add' to create a visualisation of your pipeline results. The visualisation wizard guides you through this process.", "img/tut2/11.png"));
    $scope.tutorialPages.push(makeTutorialPage("Congratulations!", "You've created your first pipeline with StreamPipes. There are many more features to explore. You can find more details in the documentation.", "img/tut2/12.png"));

    $scope.slickConfigLoaded = false;
    $scope.slickCurrentIndex = 0;
    $scope.slickConfig = {
        dots: true,
        autoplay: false,
        initialSlide: 0,
        infinite: true,
        autoplaySpeed: 1000,
        initOnload: true,
        data: $scope.tutorialPages,
        method: {},
        event: {
            beforeChange: function (event, slick, currentSlide, nextSlide) {
                console.log('before change', Math.floor((Math.random() * 10) + 100));
            },
            afterChange: function (event, slick, currentSlide, nextSlide) {
                $scope.slickCurrentIndex = currentSlide;
            },
            breakpoint: function (event, slick, breakpoint) {
                console.log('breakpoint');
            },
            destroy: function (event, slick) {
                console.log('destroy');
            },
            edge: function (event, slick, direction) {
                console.log('edge');
            },
            reInit: function (event, slick) {
                console.log('re-init');
            },
            init: function (event, slick) {
            },
            setPosition: function (evnet, slick) {
                console.log('setPosition');
            },
            swipe: function (event, slick, direction) {
                console.log('swipe');
            }
        }
    };

    $timeout(function() {
        $scope.slickConfigLoaded = true;
    }, 0);

}