import * as angular from 'angular';

export class PipelineValidationService {

    ObjectProvider: any;

    constructor(ObjectProvider) {
        this.ObjectProvider = ObjectProvider;
    }

    isValidPipeline(rawPipelineModel) {
        return this.isStreamInAssembly(rawPipelineModel) &&
            this.isActionInAssembly(rawPipelineModel) &&
            this.allElementsConnected((rawPipelineModel));
    }

    allElementsConnected(rawPipelineModel) {
        // TODO implement
        //var pipeline = this.ObjectProvider.makeFinalPipeline(rawPipelineModel);
        return true;
    }

    isStreamInAssembly(rawPipelineModel) {
        return this.isInAssembly(rawPipelineModel, "stream") || this.isInAssembly(rawPipelineModel, "set");
    }

    isActionInAssembly(rawPipelineModel) {
        return this.isInAssembly(rawPipelineModel, "action");
    }

    isInAssembly(rawPipelineModel, type) {
        var isElementInAssembly = false;
        angular.forEach(rawPipelineModel, pe => {
            if (pe.type === type) {
                isElementInAssembly = true;
            }
        });
        return isElementInAssembly;
    }
}

PipelineValidationService.$inject=['ObjectProvider']