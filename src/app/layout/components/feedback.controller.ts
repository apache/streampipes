export class FeedbackController {

    $http: any;

    closeFeedbackWindow: any;
    feedback: any = {};

    sendingFeedback: boolean = false;
    sendingFeedbackFinished: boolean = false;

    feedbackUrl = "https://www.streampipes.org/app/feedback";
    debugFeedbackUrl = "http://localhost:5000/app/feedback";

    constructor($http) {
        this.$http = $http;
    }

    $onInit() {
        this.sendingFeedback = false;
        this.sendingFeedbackFinished = false;
    }

    closeDialog() {
        this.closeFeedbackWindow();
    }

    sendFeedback() {
        this.sendingFeedback = true;
        this.$http({
            url: this.feedbackUrl,
            dataType: "json",
            data: 'email=' + encodeURIComponent(this.feedback.email) +'&feedbackText=' + encodeURIComponent(this.feedback.feedbackText),
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            method: "POST",
            withCredentials: false,
        }).then(msg => {
            this.sendingFeedbackFinished = true;
        })
    };

}