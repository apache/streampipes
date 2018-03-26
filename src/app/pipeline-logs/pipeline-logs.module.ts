import { NgModule } from '@angular/core';

import { PipelineLogsComponent } from './pipeline-logs.component';
import { LogViewRestService } from './components/logView/services/logView-rest.service';
import { LogViewComponent } from './components/logView/logView.component';

@NgModule({
    declarations: [
        PipelineLogsComponent,
        LogViewComponent
    ],
    providers: [
        LogViewRestService
    ],
    entryComponents: [
        PipelineLogsComponent
    ]
})
export class PipelineLogsModule {
}
