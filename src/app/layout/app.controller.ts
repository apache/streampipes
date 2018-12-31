import * as angular from 'angular';
import { noUndefined } from '@angular/compiler/src/util';
import 'legacy/stomp';

declare const Stomp: any;

export class AppCtrl {

    AuthStatusService: any;
    RestApi: any;
    $mdSidenav: any;
    $mdUtil: any;
    $state: any;
    $window: any;
    $location: any;
    unreadNotifications: any;
    title: any;
    notificationCount: any;
    menu: any;
    admin: any;
    toggleLeft: any;
    activePage: any;

    constructor($mdSidenav, $mdUtil, RestApi, $state, $window, $location, AuthStatusService) {
        this.AuthStatusService = AuthStatusService;
        this.RestApi = RestApi;
        this.$mdSidenav = $mdSidenav;
        this.$mdUtil = $mdUtil;
        this.$state = $state;
        this.$window = $window;
        this.$location = $location;

        this.unreadNotifications = [];
        this.title = 'StreamPipes';

        this.notificationCount = 0;

        this.AuthStatusService.userInfo = {
            Name: 'D',
            Avatar: null
        };

        this.menu = [
                {
                    link: 'streampipes',
                    title: 'Home',
                    icon: 'action:ic_home_24px'
                },
                {
                    link: 'streampipes.editor',
                    title: 'Pipeline Editor',
                    icon: 'action:ic_dashboard_24px'
                },
                {
                    link: 'streampipes.pipelines',
                    title: 'Pipelines',
                    icon: 'av:ic_play_arrow_24px'
                },
                {
                    link: 'streampipes.connect',
                    title: 'StreamPipes Connect',
                    icon: 'notification:ic_power_24px'
                },
                {
                    link: 'streampipes.dashboard',
                    title: 'Live Dashboard',
                    icon: 'editor:ic_insert_chart_24px'
                },
                {
                    link: 'streampipes.appfiledownload',
                    title: 'File Download',
                    icon: 'file:ic_file_download_24px'
                },
                {
                    link: 'streampipes.ontology',
                    title: 'Knowledge Management',
                    icon: 'social:ic_share_24px'
                },
                {
                    link: 'streampipes.sensors',
                    title: 'Pipeline Element Generator',
                    icon: 'content:ic_add_24px'
                },
                {
                    link: 'streampipes.file-upload',
                    title: 'File Upload',
                    icon: 'file:ic_file_upload_24px'
                },
            ];
            this.admin = [
                {
                    link: 'streampipes.add',
                    title: 'Install Pipeline Elements',
                    icon: 'file:ic_cloud_download_24px'
                },
                {
                    link: 'streampipes.myelements',
                    title: 'My Elements',
                    icon: 'image:ic_portrait_24px'
                },
                {
                    link: 'streampipes.configuration',
                    title: 'Configuration',
                    icon: 'action:ic_settings_24px'
                },
            ];

        this.toggleLeft = this.buildToggler('left');
            this.activePage = this.getPageTitle(this.$state.current.name);

            this.updateUnreadNotifications();
            this.connectToBroker();
        }


        authenticated() {
            return this.AuthStatusService.authenticated;
        }

        updateUnreadNotifications() {
            this.RestApi.getNotifications()
                .success(notifications => {
                    var notificationCount = 0;
                    angular.forEach(notifications, (value, key) => {
                        if (!value.read) {
                            notificationCount++;
                        }
                    });
                    this.notificationCount = notificationCount;
                })
                .error(function (msg) {
                    console.log(msg);
                });
        };

        // TODO: Function overloading?
        go(path, payload?) {
            if (payload === undefined) {
                this.$state.go(path);
                this.activePage = this.getPageTitle(path);
                this.$mdSidenav('left').close();
            } else {
                this.$state.go(path, payload);
                this.activePage = this.getPageTitle(path);
            }
        };

        logout() {
            this.RestApi.logout().then(() => {
                this.AuthStatusService.user = undefined;
                this.AuthStatusService.authenticated = false;
                this.$state.go('login');
            });
        };

        openDocumentation() {
            this.$window.open('/docs', '_blank');
        };

        openInfo() {
                this.$state.go("streampipes.info");
                this.activePage = "Info";
        }

        isActivePage(path) {
            return (this.$state.current.name == path);
        }

        getListItemClassName(path) {
                    return this.isActivePage(path) ? "sp-navbar-item-selected" : "sp-navbar-item";
                }

        getIconClassName(path) {
            return this.isActivePage(path) ? "sp-navbar-icon-selected" : "sp-navbar-icon";
        }

        getPageTitle(path) {
            var allMenuItems = this.menu.concat(this.admin);
            var currentTitle = 'StreamPipes';
            angular.forEach(allMenuItems, m => {
                if (m.link === path) {
                    currentTitle = m.title;
                }
            });
            if (path == 'streampipes.pipelineDetails') {
                currentTitle = 'Pipeline Details';
            } else if (path == 'streampipes.edit') {
                currentTitle = this.menu[0].title;
            }
            return currentTitle;
        }

        buildToggler(navID) {
            var debounceFn = this.$mdUtil.debounce(() => {
                this.$mdSidenav(navID)
                    .toggle();
            }, 300);
            return debounceFn;
        }

        getActivePage() {
          return this.activePage;
        }

        connectToBroker() {
            var login = 'admin';
            var passcode = 'admin';
            var websocketProtocol = this.$location.protocol() === "http" ? "ws" : "wss";
            var brokerUrl = websocketProtocol +'://' + this.$location.host() + ':' + this.$location.port() + '/streampipes/ws';
            var inputTopic = '/topic/org.streampipes.notifications';

            var client = Stomp.client(brokerUrl + inputTopic);

            var onConnect = (frame) => {

                client.subscribe(inputTopic, message => {
                    this.notificationCount++;
                });
            };

            client.connect(login, passcode, onConnect);
        }

    };

    //AppCtrl.$inject = ['$mdSidenav', '$mdUtil', 'RestApi', '$state', '$window', '$location', 'AuthStatusService'];
