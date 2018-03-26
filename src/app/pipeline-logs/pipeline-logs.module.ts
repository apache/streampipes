import { NgModule } from '@angular/core';

import { PipelineLogsComponent } from './pipeline-logs.component';
import { PipelineLogsRestService } from './pipeline-logs-rest.service';

@NgModule({
    declarations: [
        PipelineLogsComponent
    ],
    providers: [
        PipelineLogsRestService
    ],
    entryComponents: [
        PipelineLogsComponent
    ]
})
export class PipelineLogsModule {
}
