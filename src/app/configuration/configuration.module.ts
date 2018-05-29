import { NgModule } from '@angular/core';
import { MatButtonModule, MatCheckboxModule, MatGridListModule, MatIconModule, MatInputModule, MatTooltipModule } from '@angular/material';
import { FlexLayoutModule } from '@angular/flex-layout';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { ConfigurationComponent } from './configuration.component';
import { ConfigurationService } from './shared/configuration.service';
import { ConsulServiceComponent } from './consul-service/consul-service.component';
import {PasswordFieldComponent} from './password-field/password-field.component';
import {ConsulConfigsComponent} from './consul-configs/consul-configs.component';
import {ConsulConfigsTextComponent} from './consul-configs-text/consul-configs-text.component';
import {ConsulConfigsPasswordComponent} from './consul-configs-password/consul-configs-password.component';
import {ConsulConfigsBooleanComponent} from './consul-configs-boolean/consul-configs-boolean.component';
import {ConsulConfigsNumberComponent} from './consul-configs-number/consul-configs-number.component';



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
        ConsulServiceComponent,
        PasswordFieldComponent,
        ConsulConfigsComponent,
        ConsulConfigsTextComponent,
        ConsulConfigsPasswordComponent,
        ConsulConfigsBooleanComponent,
        ConsulConfigsNumberComponent
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