export class PipelineElementController {

    ImageChecker: any;
    ElementIconText: any;
    showImage: any;
    iconText: any;
    pipelineElement: any;
    preview: any;

    constructor(ImageChecker, ElementIconText) {
        this.ImageChecker = ImageChecker;
        this.ElementIconText = ElementIconText;

        this.showImage = false;
        this.iconText =  this.ElementIconText.getElementIconText(this.pipelineElement.name);

        this.checkImageAvailable();
    }

    checkImageAvailable() {
        console.log(this.pipelineElement);
        this.ImageChecker.imageExists(this.pipelineElement.iconUrl, (exists) => {
            this.showImage = exists;
        })
    }

    iconSizeCss() {
        if (this.preview) {
            return 'width:50px;height:50px;';
        } else {
            return 'width:80px;height:80px;';
        }
    }
}

PipelineElementController.$inject=['ImageChecker', 'ElementIconText']