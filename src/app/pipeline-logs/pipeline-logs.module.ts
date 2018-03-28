import { NgModule } from '@angular/core';

import { PipelineLogsComponent } from './pipeline-logs.component';
import { LogViewRestService } from './components/logView/services/logView-rest.service';
import { LogViewComponent } from './components/logView/logView.component';
import { NguiDatetimePickerModule } from '@ngui/datetime-picker';
import { BrowserModule } from '@angular/platform-browser';
import { CustomMaterialModule } from '../CustomMaterial/custom-material.module';


@NgModule({
    imports: [
        BrowserModule,
        NguiDatetimePickerModule,
        CustomMaterialModule,
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
