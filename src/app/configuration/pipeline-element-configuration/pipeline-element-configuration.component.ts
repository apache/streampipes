import {Component, ViewChild} from "@angular/core";
import {animate, state, style, transition, trigger} from "@angular/animations";
import {ConfigurationService} from "../shared/configuration.service";
import {StreampipesPeContainer} from "../shared/streampipes-pe-container.model";
import {StreampipesPeContainerConifgs} from "../shared/streampipes-pe-container-configs";
import {MatPaginator, MatTableDataSource} from "@angular/material";

@Component({
    selector: 'pipeline-element-configuration',
    templateUrl: './pipeline-element-configuration.component.html',
    styleUrls: ['./pipeline-element-configuration.component.css'],
    animations: [
        trigger('detailExpand', [
            state('collapsed', style({height: '0px', minHeight: '0', display: 'none'})),
            state('expanded', style({height: '*'})),
            transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
        ]),
    ]
})
export class PipelineElementConfigurationComponent {

    consulServices: StreampipesPeContainer[];

    displayedColumns: string[] = ['status', 'name', 'action'];
    @ViewChild(MatPaginator, {}) paginator: MatPaginator;
    dataSource = new MatTableDataSource<StreampipesPeContainer>();

    expandedElement: any;

    selectedConsulService: StreampipesPeContainer;
    consulServiceSelected: boolean = false;

    constructor(private configurationService: ConfigurationService) {
        this.getConsulServices();
    }

    getConsulServices(): void {
        this.configurationService.getConsulServices()
            .subscribe( response => {
                let sortedServices = this.sort(response);
                this.consulServices = sortedServices;
                this.dataSource.data = sortedServices;
            }, error => {
                console.error(error);
            });
    }

    sort(consulServices: Array<StreampipesPeContainer>):Array<StreampipesPeContainer> {
        if(!consulServices || consulServices.length === 0) return null;

        consulServices.sort((a: StreampipesPeContainer, b: StreampipesPeContainer) => {
            if (a.name < b.name) {
                return -1;
            } else if (a.name > b.name) {
                return 1;
            } else {
                return 0;
            }
        });
        consulServices.forEach(cs => cs.configs.sort((a: StreampipesPeContainerConifgs, b: StreampipesPeContainerConifgs) => {
            if (a.key < b.key) {
                return -1;
            } else if (a.key > b.key) {
                return 1;
            } else {
                return 0;
            }
        }));
        return consulServices;
    }

    updateConsulService(consulService: StreampipesPeContainer): void {
        this.configurationService.updateConsulService(consulService)
            .subscribe(response => {

            }, error => {
                console.error(error);
            });
    }

    expand(element: StreampipesPeContainer) {
        if (this.expandedElement === element) {
            this.expandedElement = undefined;
        } else {
            this.expandedElement = element;
        }
    }
}