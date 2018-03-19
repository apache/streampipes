import * as angular from 'angular';

// import { spServices } from '../services/services.module'

import { AppFileDownloadCtrl } from './app-file-download.controller'
import { AppFileDownloadRestApi } from './app-file-download-rest.service'
import { FileDetails } from './components/file-details.component'
import  'npm/angularjs-datetime-picker'

export default angular.module('sp.appFileDownload', ['angularjs-datetime-picker'])
    .controller('AppFileDownloadCtrl', AppFileDownloadCtrl)
    .service('AppFileDownloadRestApi', AppFileDownloadRestApi)
    .component('fileDetails', FileDetails)
    .name;
