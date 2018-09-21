
export class TourProviderService {

    guidedTours: any;

    // This is needed to configure the time in cypress test cases
    time: any;

    constructor(createPipelineTourConstants, dashboardTourConstants, adapterTourConstants, adapterTour2Constants) {
        this.guidedTours = [];
        this.guidedTours.push(createPipelineTourConstants.createPipelineTour);
        this.guidedTours.push(dashboardTourConstants.dashboardTour);
        this.guidedTours.push(adapterTourConstants.adapterTour);
        this.guidedTours.push(adapterTour2Constants.adapterTour);
        this.time = 500;
    }

    getAvailableTours() {
        return this.guidedTours;
    }

    getTourById(tourId) {
        return this.guidedTours.find(tour => {
            return tour.id  === tourId
        });
    }

    // This is needed to configure the time in cypress test cases
    setTime(newTime) {
        this.time = newTime;
    }

    getTime() {
        return this.time;
    }
}