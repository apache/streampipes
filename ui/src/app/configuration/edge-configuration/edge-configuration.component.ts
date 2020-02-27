import {Component} from "@angular/core";
import {ConfigurationService} from "../shared/configuration.service";
import {NodeInfo} from "../model/NodeInfo.model";

@Component({
    selector: 'edge-configuration',
    templateUrl: './edge-configuration.component.html',
    styleUrls: ['./edge-configuration.component.css']
})
export class EdgeConfigurationComponent {

    loadingCompleted: boolean = false;
    availableEdgeNodes: Array<NodeInfo>;

    constructor(private configurationService: ConfigurationService) {

    }

    ngOnInit() {
        this.getAvailableEdgeNodes();
    }

    getAvailableEdgeNodes() {
        this.configurationService.getAvailableEdgeNodes().subscribe(response => {
            this.availableEdgeNodes = response;
            console.log(this.availableEdgeNodes);
        })
    }
}