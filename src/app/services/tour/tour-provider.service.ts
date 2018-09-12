
export class TourProviderService {

    guidedTours: any;

    constructor(createPipelineTourConstants, dashboardTourConstants) {
        this.guidedTours = [];
        this.guidedTours.push(createPipelineTourConstants.createPipelineTour);
        this.guidedTours.push(dashboardTourConstants.dashboardTour);
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