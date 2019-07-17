import { AfterViewInit, Compiler, Component, Input, OnDestroy, ViewChild, ViewContainerRef, Injector } from '@angular/core';

import { InstalledApp } from '../shared/installed-app.model';

declare const SystemJS;

import * as angularCore from '@angular/core';
import * as angularCommon from '@angular/common';
SystemJS.set('@angular/core', SystemJS.newModule(angularCore));
SystemJS.set('@angular/common', SystemJS.newModule(angularCommon));

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
        /*
        const script = document.createElement('script');
        script.src = this.installedApp.bundleUrl;
        document.body.appendChild(script);
        const tile = document.createElement('app-root');
        const content = document.getElementById('plugin');
        content.appendChild(tile);
        */
       this.load();
    }

    async load() {
        const module = await SystemJS.import(this.installedApp.bundleUrl);
        const moduleFactory = await this.compiler.compileModuleAsync<any>(module["PluginAModule"]);
        const moduleRef = moduleFactory.create(this.injector);
        const componentProvider = moduleRef.injector.get('plugins');
        const componentFactory = moduleRef.componentFactoryResolver.resolveComponentFactory<any>(componentProvider[0][0].component);
        var pluginComponent = this.content.createComponent(componentFactory);
    }

}