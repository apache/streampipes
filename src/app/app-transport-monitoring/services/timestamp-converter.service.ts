import {Injectable} from "@angular/core";

@Injectable()
export class TimestampConverterService {

    constructor() {

    }

    convertTimestamp(timestamp: number): string {
        return "";
    }

    convertTimestampHoursOnly(timestamp: number): string {
        var date = new Date(timestamp);
        var hours = date.getHours();
        var minutes = "0" + date.getMinutes();
        var seconds = "0" + date.getSeconds();

        return hours + ':' + minutes.substr(-2) + ':' + seconds.substr(-2);
    }

    dateDiffHoursOnly(startTimestamp: number, endTimestamp: number):string {
        var startDate = new Date(startTimestamp);
        var endDate = new Date(endTimestamp);

        var hoursDiff = endDate.getHours() - startDate.getHours();
        var minutesDiff = "0" + (endDate.getMinutes() - startDate.getMinutes());
        var secondsDiff = "0" + (endDate.getSeconds() - startDate.getSeconds());

        return hoursDiff + ':' + minutesDiff.substr(-2) + ':' + secondsDiff.substr(-2);
    }
}