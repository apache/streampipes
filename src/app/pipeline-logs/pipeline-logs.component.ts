import { Component } from '@angular/core';

@Component({
    templateUrl: './pipeline-logs.component.html',
    styleUrls: ['./pipeline-logs.component.css']
})
export class PipelineLogsComponent {

    // TODO: Get LogSourceID from pipelinegraph

    logSourceIDs: string[];

    constructor() {
        this.logSourceIDs = new Array();
        this.logSourceIDs.push('asdsaddd');
      //  this.logSourceIDs.push('asdsaddd');
    }

    createLogView(logSourceID: string) {
        this.logSourceIDs.push(logSourceID);
    }

}
