export class PipelineElementRecommendationController {

    JsplumbService: any;
    recommendationsShown: any;
    rawPipelineModel: any;
    pipelineElementDomId: any;
    recommendedElements: any;

    constructor(JsplumbService) {
        this.JsplumbService = JsplumbService;
    }

    create(recommendedElement) {
        this.recommendationsShown = false;
        this.JsplumbService.createElement(this.rawPipelineModel, recommendedElement, this.pipelineElementDomId);
    }

    getUnskewStyle(recommendedElement, index) {
        var unskew = -(this.getSkew());
        var rotate = -(90 - (this.getSkew() / 2));

        return {
            "transform": "skew(" + unskew + "deg)" + " rotate(" + rotate + "deg)" + " scale(1)",
            "background-color": this.getBackgroundColor(recommendedElement, index)
        };
    }

    getBackgroundColor(recommendedElement, index) {
        var alpha = recommendedElement.weight < 0.2 ? 0.2 : (recommendedElement.weight - 0.2);
        var rgb = recommendedElement.type === 'sepa' ? this.getSepaColor(index) : this.getActionColor(index);

        return "rgba(" +rgb +"," +alpha +")";
    }

    getSepaColor(index) {
        return (index % 2 === 0) ? "0, 150, 136" : "0, 164, 150";
    }

    getActionColor(index) {
        return (index % 2 === 0) ? "63, 81, 181" : "79, 101, 230";
    }

    getSkewStyle(index) {
        this.fillRemainingItems();
        // transform: rotate(72deg) skew(18deg);
        var skew = this.getSkew();
        var rotate = (index + 1) * this.getAngle();

        return {
            "transform": "rotate(" + rotate + "deg) skew(" + skew + "deg)"
        };
    }

    getUnskewStyleLabel(index) {
        var unskew = -(this.getSkew());
        var rotate =  (index + 1) * this.getAngle();
        var unrotate = -360 + (rotate*-1);

        return {
            "transform": "skew(" + unskew + "deg)" + " rotate(" + unrotate + "deg)" + " scale(1)",
            "z-index": -1
        };
    }

    getSkew() {
        return (90 - this.getAngle());
    }

    getAngle() {
        return (360 / this.recommendedElements.length);
    }

    fillRemainingItems() {
        if (this.recommendedElements.length < 6) {
            for (var i = this.recommendedElements.length; i < 6; i++) {
                this.recommendedElements.push({fakeElement: true});
            }
        }
    }

}

PipelineElementRecommendationController.$inject = ['JsplumbService'];