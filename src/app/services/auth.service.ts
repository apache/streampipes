export class AuthService {

    AuthStatusService: any;
    $rootScope: any;
    $location: any;
    $state: any;
    RestApi: any;

    constructor($rootScope, $location, $state, RestApi, AuthStatusService) {
        this.AuthStatusService = AuthStatusService;
        this.$rootScope = $rootScope;
        this.$location = $location;
        this.$state = $state;
        this.RestApi = RestApi;
    }

    authenticate() {
        return this.RestApi.getAuthc()
            .then(
                response => {
                    if (response.data.success == false) {
                        this.AuthStatusService.authenticated = false;
                        this.RestApi.configured()
                            .then(response => {
                                if (response.data.configured) {
                                    this.$rootScope.appConfig = response.data.appConfig;
                                    if (!this.$location.path().startsWith("/sso") && !this.$location.path().startsWith("/streampipes/login")) {
                                        this.$state.go("login")
                                    }
                                }
                                else this.$state.go("setup")
                            })
                    }
                    else {
                        this.AuthStatusService.username = response.data.info.authc.principal.username;
                        this.AuthStatusService.email = response.data.info.authc.principal.email;
                        this.AuthStatusService.authenticated = true;
                        this.AuthStatusService.token = response.data.token;
                        this.RestApi.configured()
                            .then(response => {
                                if (response.data.configured) {
                                    this.$rootScope.appConfig = response.data.appConfig;
                                }
                            });
                        this.RestApi.getNotifications()
                            .then(notifications => {
                                this.$rootScope.unreadNotifications = notifications.data;
                            });

                    }
                },
                response => {
                    this.AuthStatusService.username = undefined;
                    this.AuthStatusService.authenticated = false;
                    this.RestApi.configured()
                        .then(conf => {
                            if (conf.data.configured) {
                            }
                            else this.$state.go("setup")
                        })
                });
    }
}

//AuthService.$inject = ['$rootScope', '$location', '$state', 'RestApi', 'AuthStatusService'];