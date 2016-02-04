var client;

var t;

function connectStomp(brokerUrl, inputTopic, propertyName) {
    var destination;

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
            $("#number").text(j[propertyName]);
        });
    };

    client.connect(login, passcode, onconnect);

    var error_callbck = function (error) {
        client.connect(login, passcode, onconnect);
    };
}


function buildTable(brokerUrl, inputTopic, bgColor, propertyName) {
    setTimeout(function () {
        var container = $("<div id='circleNumber'" +
                "style='background-color: "+ bgColor + ";border-radius: 50%; width: 150px; height: 150px; " +
                "font-size: 40px; color: #fff; line-height: 150px; text-align: center; "+
                "position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%);'>")
                .appendTo($('#container'));
        container.append("<div id='number'>##</div>");
        container.append("</div>");
    }, 2000);

    connectStomp(brokerUrl, inputTopic, propertyName);
}
	