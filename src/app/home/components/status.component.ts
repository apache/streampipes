import { Component } from "@angular/core";
import { RestApi } from "../../services/rest-api.service";

@Component({
    selector: 'status',
    templateUrl: './status.component.html',
    styleUrls: ['./status.component.css']
})
export class StatusComponent {

    pipelines: number = 0;
    runningPipelines: number = 0;
    installedPipelineElements: number = 0;

    constructor(private RestApi: RestApi) {

    }

    ngOnInit() {
        this.getPipelines();
        this.getStreams();
        this.getProcessors();
        this.getSinks();
    }

    getPipelines() {
        this.RestApi.getOwnPipelines().then(msg => {
           let pipelines = msg.data;
           this.pipelines = pipelines.length;
           this.runningPipelines = pipelines.filter(p => p.running).length;
        });
    }

    getStreams() {
        this.RestApi.getOwnSources()
            .then((msg) => {
                let sources = msg.data;
                sources.forEach((source, i, sources) => {
                    this.installedPipelineElements += source.spDataStreams.length;
                });
            });
    };

    getProcessors() {
        this.RestApi.getOwnSepas()
            .then(msg => {
                this.addPipelineElementList(msg);
            });
    };

    getSinks() {
        this.RestApi.getOwnActions()
            .then(msg => {
               this.addPipelineElementList(msg);
            });
    };

    addPipelineElementList(msg) {
        this.installedPipelineElements += msg.data.length;
    }
}