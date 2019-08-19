import { AfterViewInit, Compiler, Component, Input, ViewChild, ViewContainerRef, Injector } from '@angular/core';

import { InstalledApp } from '../shared/installed-app.model';

declare const SystemJS;

import * as angularCore from '@angular/core';
import * as angularCommon from '@angular/common';
import * as angularPlatformBrowser from '@angular/platform-browser';
import * as angularAnimations from '@angular/animations';
import * as angularCdkA11y from '@angular/cdk/a11y';
import * as angularCdkStepper from '@angular/cdk/stepper';
import * as angularCdkBidi from '@angular/cdk/bidi';
import * as angularCdkCoercion from '@angular/cdk/coercion';
import * as angularCdkPortal from '@angular/cdk/portal';
import * as angularCdkCollections from '@angular/cdk/collections';
import * as angularCdkTable from '@angular/cdk/table';
import * as angularCdkKeycodes from '@angular/cdk/keycodes';
import * as angularCdkScrolling from '@angular/cdk/scrolling';
import * as angularCdkOverlay from '@angular/cdk/overlay';
import * as angularCdkLayout from '@angular/cdk/layout';
import * as angularCdkPlatform from '@angular/cdk/platform';
import * as angularPlatformBrowserAnimations from '@angular/platform-browser/animations';
import * as angularCdk from '@angular/cdk';
import * as angularCdkObservers from '@angular/cdk/observers';
import * as angularCommonHttp from '@angular/common/http';
import * as angularMaterial from '@angular/material';
import * as angularFlexLayout from '@angular/flex-layout';
import * as angularForms from '@angular/forms';
import * as angularMaterialDialog from '@angular/material/dialog';
import * as angularMaterialStepper from '@angular/material/stepper';
import * as angularMaterialRadio from '@angular/material/radio';
import * as angularMaterialTable from '@angular/material/table';
import * as angularMaterialAutocomplete from '@angular/material/autocomplete';
import * as angularMaterialTooltip from '@angular/material/tooltip';
import * as rxjs from 'rxjs';
import * as angularCdkTree from '@angular/cdk/tree';
import * as ngxColorPicker from 'ngx-color-picker';
SystemJS.set('@angular/core', SystemJS.newModule(angularCore));
SystemJS.set('@angular/common', SystemJS.newModule(angularCommon));
SystemJS.set('@angular/platform-browser', SystemJS.newModule(angularPlatformBrowser));
SystemJS.set('@angular/animations', SystemJS.newModule(angularAnimations));
SystemJS.set('@angular/cdk/a11y', SystemJS.newModule(angularCdkA11y));
SystemJS.set('@angular/cdk/stepper', SystemJS.newModule(angularCdkStepper));
SystemJS.set('@angular/cdk/bidi', SystemJS.newModule(angularCdkBidi));
SystemJS.set('@angular/cdk/collections', SystemJS.newModule(angularCdkCollections));
SystemJS.set('@angular/cdk/coercion', SystemJS.newModule(angularCdkCoercion));
SystemJS.set('@angular/cdk/portal', SystemJS.newModule(angularCdkPortal));
SystemJS.set('@angular/cdk/table', SystemJS.newModule(angularCdkTable));
SystemJS.set('@angular/cdk/keycodes', SystemJS.newModule(angularCdkKeycodes));
SystemJS.set('@angular/cdk/scrolling', SystemJS.newModule(angularCdkScrolling));
SystemJS.set('@angular/cdk/overlay', SystemJS.newModule(angularCdkOverlay));
SystemJS.set('@angular/cdk/layout', SystemJS.newModule(angularCdkLayout));
SystemJS.set('@angular/cdk/platform', SystemJS.newModule(angularCdkPlatform));
SystemJS.set('@angular/platform-browser/animations', SystemJS.newModule(angularPlatformBrowserAnimations));
SystemJS.set('@angular/cdk', SystemJS.newModule(angularCdk));
SystemJS.set('@angular/cdk/observers', SystemJS.newModule(angularCdkObservers));
SystemJS.set('@angular/common/http', SystemJS.newModule(angularCommonHttp));
SystemJS.set('@angular/material', SystemJS.newModule(angularMaterial));
SystemJS.set('@angular/flex-layout', SystemJS.newModule(angularFlexLayout));
SystemJS.set('@angular/forms', SystemJS.newModule(angularForms));
SystemJS.set('@angular/material/dialog', SystemJS.newModule(angularMaterialDialog));
SystemJS.set('@angular/material/stepper', SystemJS.newModule(angularMaterialStepper));
SystemJS.set('@angular/material/radio', SystemJS.newModule(angularMaterialRadio));
SystemJS.set('@angular/material/table', SystemJS.newModule(angularMaterialTable));
SystemJS.set('@angular/material/autocomplete', SystemJS.newModule(angularMaterialAutocomplete));
SystemJS.set('@angular/material/tooltip', SystemJS.newModule(angularMaterialTooltip));
SystemJS.set('rxjs', SystemJS.newModule(rxjs));
SystemJS.set('@angular/cdk/tree', SystemJS.newModule(angularCdkTree));
SystemJS.set('ngx-color-picker', SystemJS.newModule(ngxColorPicker));

@Component({
    selector: 'view',
    templateUrl: './view.component.html',
    styleUrls: ['./view.component.css']
})
export class ViewComponent implements AfterViewInit {

    @Input() installedApp: InstalledApp;
    @ViewChild('pluginHost', { read: ViewContainerRef }) content: ViewContainerRef;

    constructor(private compiler: Compiler, private injector: Injector) {
    }

    ngAfterViewInit(): void {
        this.load();
    }

    async load() {
        const module = await SystemJS.import('/streampipes-apps' + this.installedApp.entry);
        const moduleFactory = await this.compiler.compileModuleAndAllComponentsAsync<any>(module["AppModule"]);
        const moduleRef = moduleFactory.ngModuleFactory.create(this.injector);
        for (const component of moduleFactory.componentFactories) {
            if (component.selector === 'app-component') {
                this.content.createComponent(component, null, this.injector, [], moduleRef)
            }
        }
    }

}