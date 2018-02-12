export class PipelineElementController {

    constructor(ImageChecker, ElementIconText) {
        this.ImageChecker = ImageChecker;
        this.ElementIconText = ElementIconText;

        this.showImage = false;
        this.iconText =  this.ElementIconText.getElementIconText(this.pipelineElement.payload.name);

        this.checkImageAvailable();
    }

    checkImageAvailable() {
        this.ImageChecker.imageExists(this.pipelineElement.payload.iconUrl, (exists) => {
            this.showImage = exists;
        })
    }

}

PipelineElementController.$inject=['ImageChecker', 'ElementIconText']