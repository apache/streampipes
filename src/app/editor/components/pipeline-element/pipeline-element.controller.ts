export class PipelineElementController {

    ImageChecker: any;
    ElementIconText: any;
    showImage: any;
    iconText: any;
    pipelineElement: any;
    preview: any;
    iconSize: any;
    iconStandSize: any;

    constructor(ImageChecker, ElementIconText) {
        this.ImageChecker = ImageChecker;
        this.ElementIconText = ElementIconText;
        this.showImage = false;
    }

    $onInit() {
        this.iconText =  this.ElementIconText.getElementIconText(this.pipelineElement.name);
        this.checkImageAvailable();
    }

    checkImageAvailable() {
        this.ImageChecker.imageExists(this.pipelineElement.iconUrl, (exists) => {
            this.showImage = exists;
        })
    }

    iconSizeCss() {
        if (this.iconSize) {
            return 'width:35px;height:35px;';
        }
        else if (this.preview) {
            return 'width:50px;height:50px;';
        } else if (this.iconStandSize) {
            return 'width:50px;height:50px;margin-top:-5px;'
        } else {
            return 'width:70px;height:70px;';
        }
    }
}

PipelineElementController.$inject=['ImageChecker', 'ElementIconText']