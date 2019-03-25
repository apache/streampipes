export interface ParcelInfoEventModel {
    date: string;
    timestamp: number;
    number_of_cardboard_boxes: number;
    number_of_detected_boxes: number;
    number_of_transparent_boxes: number;
    segmentationImage: string;
}