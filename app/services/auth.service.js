export class AuthService {

    constructor($rootScope, $location, $state, RestApi) {
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
                        this.$rootScope.authenticated = false;
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
                        this.$rootScope.username = response.data.info.authc.principal.username;
                        this.$rootScope.email = response.data.info.authc.principal.email;
                        this.$rootScope.authenticated = true;
                        this.$rootScope.token = response.data.token;
                        this.RestApi.configured()
                            .then(response => {
                                if (response.data.configured) {
                                    this.$rootScope.appConfig = response.data.appConfig;
                                }
                            });
                        this.RestApi.getNotifications()
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
                    this.RestApi.configured()
                        .then(conf => {
                            if (conf.data.configured) {
                            }
                            else this.$state.go("setup")
                        })
                });
    }
}

AuthService.$inject = ['$rootScope', '$location', '$state', 'RestApi'];
