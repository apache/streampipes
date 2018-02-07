export class AuthService {

    constructor($rootScope, $location, $state, restApi) {
        this.$rootScope = $rootScope;
        this.$location = $location;
        this.$state = $state;
        this.restApi = restApi;
    }

    authenticate() {
        return this.restApi.getAuthc()
            .then(
                response => {
                    if (response.data.success == false) {
                        this.$rootScope.authenticated = false;
                        this.restApi.configured()
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
                        this.$rootScope.username = response.data.info.authc.principal.username;
                        this.$rootScope.email = response.data.info.authc.principal.email;
                        this.$rootScope.authenticated = true;
                        this.$rootScope.token = response.data.token;
                        this.restApi.configured()
                            .then(response => {
                                if (response.data.configured) {
                                    this.$rootScope.appConfig = response.data.appConfig;
                                }
                            });
                        this.restApi.getNotifications()
                            .success(notifications => {
                                this.$rootScope.unreadNotifications = notifications
                            })
                            .error(msg => {
                                console.log(msg);
                            });

                    }
                },
                response => {
                    this.$rootScope.username = undefined;
                    this.$rootScope.authenticated = false;
                    this.restApi.configured()
                        .then(conf => {
                            if (conf.data.configured) {
                            }
                            else this.$state.go("setup")
                        })
                });
    }
}

AuthService.$inject = ['$rootScope', '$location', '$state', 'restApi'];
