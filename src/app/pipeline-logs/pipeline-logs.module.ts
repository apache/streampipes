import { NgModule } from '@angular/core';

import { PipelineLogsComponent } from './pipeline-logs.component';
import { LogViewRestService } from './components/logView/services/logView-rest.service';
import { LogViewComponent } from './components/logView/logView.component';
import { MatIconModule, MatInputModule} from '@angular/material';
import { NguiDatetimePickerModule } from '@ngui/datetime-picker';
import { BrowserModule } from '@angular/platform-browser';


@NgModule({
    imports: [
        BrowserModule,
        MatInputModule,
        MatIconModule,
        NguiDatetimePickerModule
    ],
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
