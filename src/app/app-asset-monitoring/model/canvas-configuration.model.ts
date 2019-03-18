import {DashboardConfiguration} from "./dashboard-configuration.model";
import Konva from "konva";

export interface CanvasConfiguration {
    file: File;
    dashboardCanvas: Konva.Stage;
}