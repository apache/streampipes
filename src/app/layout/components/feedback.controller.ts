/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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