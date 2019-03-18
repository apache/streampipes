import {CanvasElement} from "./canvas-element.model";
import {CanvasAttributes} from "./canvas-attributes.model";
import {ImageInfo} from "./image-info.model";

export interface DashboardConfiguration {
    dashboardName: string;
    dashboardDescription: string;
    imageInfo: ImageInfo;
    attrs: CanvasAttributes;
    className: string;
    children: CanvasElement[];

}