import angular from 'angular';
import uiRouter from 'angular-ui-router';
import 'angular-material-icons';
import 'angular-material';
import 'angular-loading-bar';

import stateConfig from './state.config'
import iconProviderConfig from './icon-provider.config'
import httpProviderConfig from './http-provider.config'

export default angular.module('sp.core', [uiRouter, 'ngMaterial', 'ngMdIcons', 'angular-loading-bar'])
    .config(stateConfig)
    .config(iconProviderConfig)
    .config(httpProviderConfig)
    .name;