import * as angular from 'angular';
import 'angular-material-icons';
import 'angular-material';
import 'angular-loading-bar';

import stateConfig from './state.config'
import iconProviderConfig from './icon-provider.config'
import httpProviderConfig from './http-provider.config'

export default angular.module('sp.core', ['ui.router', 'ui.router.upgrade', 'ngMaterial', 'ngMdIcons', 'angular-loading-bar'])
    .config(stateConfig)
    .config(iconProviderConfig)
    .config(httpProviderConfig)
    .name;