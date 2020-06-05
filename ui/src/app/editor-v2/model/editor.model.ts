import {
  DataProcessorInvocation,
  DataSinkInvocation,
  SpDataStream
} from "../../core-model/gen/streampipes-model";

export interface PipelineElementHolder {
  [key: string]: (SpDataStream | DataProcessorInvocation | DataSinkInvocation);
}