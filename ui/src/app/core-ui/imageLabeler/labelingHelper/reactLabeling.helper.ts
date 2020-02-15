export class ReactLabelingHelper {

  //mouse position
  private static lastMouseX = 0;
  private static lastMouseY = 0;
  private static lastMouseXTransformed = 0;
  private static lastMouseYTransformed = 0;
  private static isMouseDown = false;

  private static reactHeight = 0;
  private static reactWidth = 0;

  static mouseDown(mousePos, mousePosTransformed) {
    this.lastMouseX = mousePos[0];
    this.lastMouseY = mousePos[1];
    this.lastMouseXTransformed = mousePosTransformed[0];
    this.lastMouseYTransformed = mousePosTransformed[1];
    this.isMouseDown = true;
    console.log(this.lastMouseXTransformed)
  }

  static mouseMove(mousePos, mousePosTransformed, context, label, color) {
    let mouseX = mousePos[0];
    let mouseY = mousePos[1];

    if(this.isMouseDown) {
      this.reactWidth = mouseX - this.lastMouseX;
      this.reactHeight = mouseY - this.lastMouseY;
      context.strokeStyle = color;
      context.beginPath();
      context.rect(this.lastMouseX, this.lastMouseY, this.reactWidth, this.reactHeight);
      context.fillText(label, this.lastMouseX, this.lastMouseY + this.reactHeight);
      context.stroke();
    }
  }

  static mouseUp(mousePos, mousePosTransformed, coco, labelId) {
    this.isMouseDown = false;
    let reactWidth = mousePosTransformed[0] - this.lastMouseXTransformed;
    let reactHeight = mousePosTransformed[1] - this.lastMouseYTransformed;

    coco.addReactAnnotation(this.lastMouseXTransformed, this.lastMouseYTransformed, reactWidth, reactHeight, labelId);
    console.log('Add react Label:', this.lastMouseXTransformed, this.lastMouseYTransformed, reactWidth, reactHeight, labelId)
  }

  static draw(annotation,label, context, color, imageXShift, imageYShift) {
    context.strokeStyle = color;
    context.beginPath();
    let bbox = annotation.bbox;
    context.rect(bbox[0] + imageXShift, bbox[1] + imageYShift, bbox[2], bbox[3]);
    context.fillText(label, bbox[0] + imageXShift, bbox[1] + bbox[3] + imageYShift);
    context.stroke();
  }

}