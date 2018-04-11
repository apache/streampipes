import {AfterViewInit, Component, Input, SimpleChanges} from '@angular/core';
import { PipelineLogsRestService } from './components/services/pipeline-logs-rest.service';

@Component({
    templateUrl: './pipeline-logs.component.html',
    styleUrls: ['./pipeline-logs.component.css']
})
export class PipelineLogsComponent implements AfterViewInit {

    @Input() pipelineID: string;

    pipelineName: string;

    logSourceIDs: string[];
    logSourceIDsSelect = [];
    logSourceIDsSelected = 'ALL';

    constructor(private pipelineLogsRestService: PipelineLogsRestService) {

    }

    ngAfterViewInit() {
    }

    // TODO: Move this all to AfterViewInit if will get pipelineID via Input, and input field is not used anymore
    createLogView(pipelineID: string) {

        this.pipelineLogsRestService.getPipelineElement(pipelineID)
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
