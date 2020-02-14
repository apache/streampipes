export class Annotation {
  id: number;
  image_id: number;
  category_id: number;
  segmentation: []; //RLE or [polygon]
  area: number;
  bbox: [number,number,number,number]; //[x,y,width,height]
  iscrowd: number; //(iscrowd=0 in which case polygons are used) or a collection of objects (iscrowd=1 in which case RLE is used)
}