import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class LabelService {

  constructor() { }

  getLabels() {
    return {
      'boxes': ['blue', 'red'],
      'sign': ['trafficsign'],
      'person': ['person', 'Child'],
      'vehicle': ['bicycle', 'car', 'motorcycle', 'airplane', 'bus', 'train', 'truck', 'boat'],
      'outdoor': ['traffic light', 'fire hydrant', 'stop sign', 'parking meter', 'bench'],
      'animal': ['bird', 'cat', 'dog'],
      'accessory': ['backpack', 'umbrella', 'handbag', 'suitcase'],
      'sports': ['frisbee', 'sports ball', 'skis', 'frisbee', 'baseball bat'],
      'kitchen': ['bottle', 'cup', 'fork', 'knife', 'spoon'],
      'furniture': ['chair', 'couch', 'bed', 'table'],
      'electronic': ['tv', 'laptop', 'mouse', 'keyboard']
    };
  }

}
