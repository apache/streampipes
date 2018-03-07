import { AfterViewInit, Compiler, Component, Input, OnDestroy, ViewChild, ViewContainerRef } from '@angular/core';

import { InstalledApp } from '../shared/installed-app.model';

// This Imports will be exposed to the dynamically loaded Apps
import * as AngularCore from '@angular/core';
import * as AngularCommon from '@angular/common';

@Component({
    selector: 'view',
    templateUrl: './view.component.html',
    styleUrls: ['./view.component.css']
})
export class ViewComponent implements AfterViewInit {

    @Input() installedApp: InstalledApp;
    @ViewChild('pluginHost') pluginHost;

    constructor(private compiler: Compiler, private vcr: ViewContainerRef) {
    }

    ngAfterViewInit(): void {
        fetch(this.installedApp.bundleUrl)
            .then(response => response.text())
            .then(source => {
                const exports = {};
                const modules = {
                    '@angular/core': AngularCore,
                    '@angular/common': AngularCommon
                };
                const require = (module) => modules[module];

                eval(source);

                const mwcf = this.compiler.compileModuleAndAllComponentsSync(exports[this.installedApp.moduleName]);

                const componentFactory = mwcf.componentFactories
                    .find(e => e.selector === this.installedApp.selector);

                if (componentFactory) {
                    this.vcr.clear();

                    const componentRef = this.vcr.createComponent(componentFactory);
                }
            });
    }

}