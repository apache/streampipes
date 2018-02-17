import { Ng1AppModule } from './app/app.module';
import { AppModule } from './appng5/app.module';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

platformBrowserDynamic().bootstrapModule(AppModule).then(ref => {
    (<any>ref.instance).upgrade.bootstrap(document.body, [Ng1AppModule.name]);
});