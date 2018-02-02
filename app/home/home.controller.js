export class HomeCtrl {

    constructor(HomeService) {
        this.homeService = HomeService;
        this.serviceLinks = this.homeService.getServiceLinks();
    }

    openLink(l) {
        this.homeService.openLink(l);
    }

}

HomeCtrl.$inject = ['HomeService'];