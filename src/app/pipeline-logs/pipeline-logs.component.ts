import { AfterViewInit, Component } from '@angular/core';
import { PipelineLogsRestService } from './components/services/pipeline-logs-rest.service';

@Component({
    templateUrl: './pipeline-logs.component.html',
    styleUrls: ['./pipeline-logs.component.css']
})
export class PipelineLogsComponent implements AfterViewInit {


    pipelineName: string;
    pipelineID;

    logSourceIDs: string[];
    logSourceIDsSelect = [];
    logSourceIDsSelected = 'ALL';

    constructor(private pipelineLogsRestService: PipelineLogsRestService) {
    }

    ngAfterViewInit() {
        let url: string = window.location.href;

        url = url.replace('/logs', '');
        const lastSlash = url.lastIndexOf('/');

        this.pipelineID = url.substring(lastSlash + 1);


        this.pipelineLogsRestService.getPipelineElement(this.pipelineID)
            .subscribe( graph => {
                const actions = (<Graph> graph).actions;
                const sepas = (<Graph> graph).sepas;
                this.pipelineName = (<Graph> graph).name;

                actions.forEach( action => {
                    this.logSourceIDsSelect.push({
                        name: action.name + ' - ' + action.uri,
                        uri: action.uri,
                    });
                });
                sepas.forEach( sepa => {
                    this.logSourceIDsSelect.push({
                        name: sepa.name + ' - ' + sepa.uri,
                        uri: sepa.uri,
                    });
                });

                this.logSourcelSelection();

            }, error => {
                console.log(error);
            });
    }


    logSourcelSelection() {
        if (this.logSourceIDsSelected === 'ALL') {
            this.logSourceIDs = this.logSourceIDsSelect.map(t => t.uri);
        } else {
            this.logSourceIDs = [];
            this.logSourceIDs.push(this.logSourceIDsSelected);
        }
    }

}

interface Graph {
    actions;
    name;
    sepas;
}
