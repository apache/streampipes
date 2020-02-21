export class Annotation {
  id: number;
  image_id: number;
  category_id: number;
  segmentation: []; //RLE or [polygon]
  area: number;
  bbox: [number,number,number,number]; //[x,y,width,height]
  iscrowd: number; //(iscrowd=0 in which case polygons are used) or a collection of objects (iscrowd=1 in which case RLE is used)

  //For UI
  isSelected: boolean = false;
  isHovered: boolean = false;

  checkIfInArea(cords): boolean {
    if (this.bbox !== undefined) {
      if (this.bbox[0] <= cords[0] && cords[0] <= this.bbox[0] + this.bbox[2] &&
          this.bbox[1] <= cords[1] && cords[1] <= this.bbox[1] + this.bbox[3]
      ) {
        return true;
      }
    }
    return false;
    //TODO Polygon
  }

}