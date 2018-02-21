import { Component } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { HomeService } from './home.service';
import { StateService } from '@uirouter/core';

@Component({
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.css']
})
export class HomeComponent {

    serviceLinks = [];

    constructor(private homeService: HomeService, private sanitizer: DomSanitizer, private stateService: StateService) {
        this.serviceLinks = this.homeService.getServiceLinks();
    }

    getBackground(url) {
        return this.sanitizer.bypassSecurityTrustStyle(`url(${url})`);
    }

    openLink(link) {
        if (link.link.newWindow) {
            window.open(link.link.value);
        } else {
            this.stateService.go(link.link.value);
        }
    }

}