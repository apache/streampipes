import * as angular from 'angular';

export class PipelineValidationService {

    ObjectProvider: any;

    constructor(ObjectProvider) {
        this.ObjectProvider = ObjectProvider;
    }

    isValidPipeline(rawPipelineModel) {
        console.log(rawPipelineModel);
        return this.isStreamInAssembly(rawPipelineModel) &&
            this.isActionInAssembly(rawPipelineModel) &&
            this.allElementsConnected((rawPipelineModel));
    }

    allElementsConnected(rawPipelineModel) {
        var pipeline = this.ObjectProvider.makeFinalPipeline(angular.copy(rawPipelineModel));
        return pipeline.actions.every(a => a.connectedTo && a.connectedTo.length > 0) &&
            pipeline.sepas.every(s => s.connectedTo && s.connectedTo.length > 0);
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
            if (pe.type === type && !pe.settings.disabled) {
                isElementInAssembly = true;
            }
        });
        return isElementInAssembly;
    }
}

PipelineValidationService.$inject=['ObjectProvider']