export class OutputStrategyController {

    constructor() {
        this.outputStrategyTypes = [{label: "Append", "type": "org.streampipes.model.output.AppendOutputStrategy"},
            {label: "Custom", "type": "org.streampipes.model.output.CustomOutputStrategy"},
            {label: "Fixed", "type": "org.streampipes.model.output.FixedOutputStrategy"},
            {label: "List", "type": "org.streampipes.model.output.ListOutputStrategy"},
            {label: "Keep", "type": "org.streampipes.model.output.RenameOutputStrategy"}];

        this.selectedOutputStrategy = this.outputStrategyTypes[0].type;
    }

    addOutputStrategy(strategies) {
        if (strategies == undefined) this.strategies = [];
        this.strategies.push(getNewOutputStrategy());
    }

    removeOutputStrategy(strategies, index) {
        strategies.splice(index, 1);
    }

    getNewOutputStrategy() {
        if (this.selectedOutputStrategy === this.outputStrategyTypes[0].type)
            return {"type": this.outputStrategyTypes[0].type, "properties": {"eventProperties": []}};
        else if (this.selectedOutputStrategy === this.outputStrategyTypes[1].type)
            return {"type": this.outputStrategyTypes[1].type, "properties": {"eventProperties": []}};
        else if (this.selectedOutputStrategy === this.outputStrategyTypes[2].type)
            return {"type": this.outputStrategyTypes[2].type, "properties": {"eventProperties": []}};
        else if (this.selectedOutputStrategy === this.outputStrategyTypes[3].type)
            return {"type": this.outputStrategyTypes[3].type, "properties": {}};
        else if (this.selectedOutputStrategy === this.outputStrategyTypes[4].type)
            return {"type": this.outputStrategyTypes[4].type, "properties": {}};

    }

    getType(strategy) {
        var label;
        angular.forEach(this.outputStrategyTypes, function (value) {
            if (value.type == strategy.type) label = value.label;
        });
        return label;
    }
}