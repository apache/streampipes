import angular from 'npm/angular';

import spServices from '../services/services.module'

import AppFileDownloadCtrl from './app-file-download.controller'
import AppFileDownloadRestService from './app-file-download-rest.service'
import fileDetails from './directives/file-details.directive'

export default angular.module('sp.appfiledownload', [spServices])
    .controller('AppFileDownloadCtrl', AppFileDownloadCtrl)
    .factory('appFileDownloadRestService', AppFileDownloadRestService)
    .directive('fileDetails', fileDetails)
    .name;
