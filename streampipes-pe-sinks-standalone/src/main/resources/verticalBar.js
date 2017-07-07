var client;

var t;

function connectStomp(brokerUrl, inputTopic, minValue, maxValue, propertyName) {
        var destination;
        t = 0;

        var login = 'admin';
        var passcode = 'admin';
        client = Stomp.client(brokerUrl);
        // this allows to display debug logs directly on the web page
        client.debug = function (str) {
                console.log(str);
        };

        console.log(passcode);
        console.log(brokerUrl);
        // the client is notified when it is connected to the server.
        var onconnect = function (frame) {

                console.log("connected to Stomp");
                console.log("destination: " + inputTopic);
                client.subscribe(inputTopic, function (message) {
                        var j = jQuery.parseJSON(message.body);
                        console.log(message.body);
                        var printValue = 100 - percent(minValue, maxValue, j[propertyName]);
                        $("#currentFillPercent").css("height", printValue + "%");
                        $("#legendSpace").css("height", printValue + "%");
                        $("#valueCurrent").text(j[propertyName]);
                });
        };

        client.connect(login, passcode, onconnect);

        var error_callbck = function (error) {
                client.connect(login, passcode, onconnect);
        };
}


function buildTable(brokerUrl, inputTopic, bgColor, minValue, maxValue, propertyName) {
        setTimeout(function () {
                //HTML Structure
                $("<div id='measuringCylinder'>" +
                        "<div id='verticalBar'>"+
                                "<div id='barTop'></div>"+
                                "<div id='currentFill'>" +
                                        "<div id='currentFillPercent'></div>" +
                                        "<div id='marker'></div>" +
                                "</div>" +
                                "<div id='barBottom'></div>"+
                        "</div>"+
                        "<div id='legend'>" +
                                "<div id='valueTop'>" + maxValue + "</div>"+
                                "<div id='space'>" +
                                        "<div id='legendSpace'></div>"+
                                        "<div id='valueCurrent'>###</div>"+
                                "</div>"+
                                "<div id='valueBottom'>" + minValue + "</div>"+
                        "</div>" +
                "</div>").appendTo($("#container"));
                //CSS
                $("#measuringCylinder").css({
                        "position": "absolute",
                        "top": "50%",
                        "left": "50%",
                        "transform": "translate(-50%, -50%)"
                });
                $("#verticalBar").css({
                        "float": "left"
                });
                $("#barTop").css({
                        "border-top": "2px " + bgColor + " solid",
                        "border-right": "15px " + bgColor + " solid"
                }).width(60);
                $("#barBottom").css({
                        "border-top": "2px " + bgColor + " solid",
                        "border-right": "15px " + bgColor + " solid"
                }).width(60);                
                $("#marker").css({
                        "border-top": "2px " + "black" + " solid",
                        "border-right": "15px " + "black" + " solid",
                        "margin-top": "-1px"
                }).width(60);
                $("#currentFill").css({
                        "width": "60px",
                        "border-width": "2px",
                        "border-style": "solid",
                        "-webkit-border-radius": "2px",
                        "-moz-border-radius": "2px",
                        "border-radius": "2px",
                        "background-color": bgColor,
                        "border-color": bgColor,
                        "margin":"-2px"
                }).height(200);
                $("#currentFillPercent").css({
                        "background-color": "white",
                        "max-height": "100%"
                });
                $("#legend").css({
                        "float": "left",
                        "position": "relative",
//                        "text-align": "right",
                        "margin-top": "-9px"
                }).height(222);
                $("#valueTop").css({
                        "color": bgColor
                });
                $("#space").css({
                        "margin-top": "-16px"
                }).height(200);
                $("#legendSpace").css({
                        "min-height": "10px",
                        "max-height": "189px"
                });
                $("#valueBottom").css({
                        "color": bgColor,
                        "position": "absolute",
                        "bottom": 0
                });
        }, 2000);

        connectStomp(brokerUrl, inputTopic, minValue, maxValue, propertyName);
}

function percent(min, max, current) {
        return 100 * (current - min) / (max - min);
}
	