import { EventProperty } from "./EventProperty";

export class ExampleNode extends EventProperty {
    name: string;
    level: number;
    expandable: boolean;
    isExpanded?: boolean;
    child = undefined;
    parent = undefined;

    public copy(): EventProperty {
      throw new Error("Method not implemented.");
    }
  }