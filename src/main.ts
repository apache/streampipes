import 'hammerjs';

import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { UrlService } from '@uirouter/core';

import { AppModule } from './app/appng5.module';

platformBrowserDynamic().bootstrapModule(AppModule).then(platformRef => {
    const url: UrlService = platformRef.injector.get(UrlService);
    url.listen();
    url.sync();
});