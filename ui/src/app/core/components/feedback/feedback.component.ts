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

import {Component, EventEmitter, OnInit, Output} from "@angular/core";

@Component({
    selector: 'feedback',
    templateUrl: './feedback.component.html',
})
export class FeedbackComponent implements OnInit {

    @Output()
    closeFeedbackEmitter: EventEmitter<void> = new EventEmitter<void>();

    feedback: any = {};

    sendingFeedback: boolean = false;
    sendingFeedbackFinished: boolean = false;

    // deactivate direct feedback for Apache transition
    feedbackUrl = "";
    debugFeedbackUrl = "";

    targetEmail = "dev@streampipes.apache.org";

    constructor() {
    }

    ngOnInit() {
        this.sendingFeedback = false;
        this.sendingFeedbackFinished = false;
    }

    closeDialog() {
        this.closeFeedbackEmitter.emit();
        this.sendingFeedback = false;
        this.sendingFeedbackFinished = false;
    }

    sendMail(){
        this.sendingFeedback = true;
        window.open("mailto:"+ this.targetEmail + "?subject=" +"[USER-FEEDBACK]" +"&body=" +this.feedback.feedbackText, "_self");
        this.sendingFeedbackFinished = true;
    };
}