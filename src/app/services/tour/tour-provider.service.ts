
export class TourProviderService {

    guidedTours: any;

    constructor(createPipelineTourConstants, dashboardTourConstants, adapterTourConstants) {
        this.guidedTours = [];
        this.guidedTours.push(createPipelineTourConstants.createPipelineTour);
        this.guidedTours.push(dashboardTourConstants.dashboardTour);
        this.guidedTours.push(adapterTourConstants.adapterTour);
    }

    getAvailableTours() {
        return this.guidedTours;
    }

    getTourById(tourId) {
        return this.guidedTours.find(tour => {
            return tour.id  === tourId
        });
    }
}