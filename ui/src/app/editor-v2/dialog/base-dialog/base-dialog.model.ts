import {PanelDialogComponent} from "../panel-dialog/panel-dialog.component";
import {StandardDialogComponent} from "../standard-dialog/standard-dialog.component";

export type BaseDialogComponentUnion = PanelDialogComponent<unknown> | StandardDialogComponent<unknown>;

export enum PanelType {
  STANDARD_PANEL,
  SLIDE_IN_PANEL
}

export interface DialogConfig {
  width?: string;
  panelType: PanelType;
  disableClose?: boolean;
  autoFocus?: boolean;
  title: string;
}

export interface DialogPanelConfig {
  maxWidth: string,
  height: string,
}