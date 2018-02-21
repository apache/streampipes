import { NgModule } from '@angular/core';
import { MatButtonModule, MatCheckboxModule, MatGridListModule, MatIconModule, MatInputModule, MatTooltipModule } from '@angular/material';
import { FlexLayoutModule } from '@angular/flex-layout';
import { CommonModule } from '@angular/common';
import { ConfigurationComponent } from './configuration.component';
import { ConfigurationService } from './configuration.service';
import { ConsulServiceComponent } from './components/consul-service/consul-service.component';
import { FormsModule } from '@angular/forms';

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