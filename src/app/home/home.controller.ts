export class HomeCtrl {

    homeService: any;
    serviceLinks: any;

    constructor(HomeService) {
        this.homeService = HomeService;
        this.serviceLinks = this.homeService.getServiceLinks();
    }

    openLink(l) {
        this.homeService.openLink(l);
    }

}

//HomeCtrl.$inject = ['HomeService'];