import { NgModule } from '@angular/core';
import {TsonLdSerializerService} from './tsonld-serializer.service';
import {PipelineTemplateService} from './apis/pipeline-template.service';

@NgModule({
    imports: [
    ],
    declarations: [
    ],
    providers: [
        TsonLdSerializerService,
        PipelineTemplateService
    ],
    entryComponents: [
    ]
})
export class PlatformServicesModule {
}