import 'core-js/es7/reflect';
import 'zone.js/dist/zone';

import { Ng1AppModule } from './app/app.module';

import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { AppModule } from "./appng5/app.module";

platformBrowserDynamic().bootstrapModule(AppModule).then(ref => {
    (<any>ref.instance).upgrade.bootstrap(document.body, [Ng1AppModule.name]);
});