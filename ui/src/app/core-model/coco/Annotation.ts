export class Annotation {
  id: number;
  image_id: number;
  category_id: number;
  segmentation: [any]; //RLE or [polygon] -> polygon [[x1,y1,x2,y2,... xn, yn]]
  area: number;
  bbox: [number,number,number,number]; //[x,y,width,height]
  iscrowd: number; //(iscrowd=0 in which case polygons are used) or a collection of objects (iscrowd=1 in which case RLE is used)

  //For UI
  isSelected: boolean = false;
  isHovered: boolean = false;


  constructor() {
    this.segmentation = undefined;
    this.bbox = undefined;
  }

  isBox() {
    return this.bbox !== undefined;
  }


  isPolygon() {
    return this.segmentation !== undefined;
  }

}