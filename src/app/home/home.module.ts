import * as angular from 'angular';

import { HomeService } from './home.service';
import { HomeComponent } from './home.component';
import { downgradeComponent, downgradeInjectable } from '@angular/upgrade/static';

export default angular.module('sp.home', [])
    .directive('homeComponent', downgradeComponent({component: HomeComponent}))
    .service('HomeService', downgradeInjectable(HomeService))
    .name;
