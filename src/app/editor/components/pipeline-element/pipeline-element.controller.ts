export class PipelineElementController {

    ImageChecker: any;
    ElementIconText: any;
    showImage: any;
    iconText: any;
    pipelineElement: any;

    constructor(ImageChecker, ElementIconText) {
        this.ImageChecker = ImageChecker;
        this.ElementIconText = ElementIconText;

        this.showImage = false;
        this.iconText =  this.ElementIconText.getElementIconText(this.pipelineElement.name);

        this.checkImageAvailable();
    }

    checkImageAvailable() {
        this.ImageChecker.imageExists(this.pipelineElement.payload.iconUrl, (exists) => {
            this.showImage = exists;
        })
    }

}

PipelineElementController.$inject=['ImageChecker', 'ElementIconText']