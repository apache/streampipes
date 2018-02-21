import { NgModule } from '@angular/core';
import { MatButtonModule, MatCheckboxModule, MatGridListModule, MatIconModule, MatInputModule, MatTooltipModule } from '@angular/material';
import { FlexLayoutModule } from '@angular/flex-layout';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { ConfigurationComponent } from './configuration.component';
import { ConfigurationService } from './shared/configuration.service';
import { ConsulServiceComponent } from './consul-service/consul-service.component';

@NgModule({
    imports: [
        CommonModule,
        FlexLayoutModule,
        MatGridListModule,
        MatButtonModule,
        MatIconModule,
        MatInputModule,
        MatCheckboxModule,
        MatTooltipModule,
        FormsModule
    ],
    declarations: [
        ConfigurationComponent,
        ConsulServiceComponent
    ],
    providers: [
        ConfigurationService
    ],
    entryComponents: [
        ConfigurationComponent
    ]
})
export class ConfigurationModule {
}