export class StreamRestrictionController {

    constructor() {

    }

    addStreamRestriction(streams) {
        if (streams == undefined) streams = [];
        streams.push({"eventSchema": {"eventProperties": []}});
    }

    removeStreamRestriction(streamIndex, streams) {
        streams.splice(streamIndex, 1);
    }
}