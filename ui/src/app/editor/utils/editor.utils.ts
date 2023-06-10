/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import { EditorConstants } from '../constants/editor.constants';
import {
    PipelineElementType,
    PipelineElementUnion,
} from '../model/editor.model';
import {
    DataProcessorInvocation,
    DataSinkInvocation,
    SpDataStream,
} from '@streampipes/platform-services';

export class PipelineElementTypeUtils {
    static toClassName(element: PipelineElementType): string {
        if (element === PipelineElementType.DataStream) {
            return EditorConstants.DATA_STREAM_IDENTIFIER;
        } else if (element === PipelineElementType.DataProcessor) {
            return EditorConstants.DATA_PROCESSOR_IDENTIFIER;
        } else {
            return EditorConstants.DATA_SINK_IDENTIFIER;
        }
    }

    static fromClassName(className: string): PipelineElementType {
        if (className === EditorConstants.DATA_STREAM_IDENTIFIER) {
            return PipelineElementType.DataStream;
        } else if (className === EditorConstants.DATA_PROCESSOR_IDENTIFIER) {
            return PipelineElementType.DataProcessor;
        } else {
            return PipelineElementType.DataSink;
        }
    }

    static toCssShortHand(elementType: PipelineElementType) {
        if (PipelineElementType.DataStream === elementType) {
            return 'stream';
        } else if (PipelineElementType.DataSet === elementType) {
            return 'set';
        } else if (PipelineElementType.DataProcessor === elementType) {
            return 'sepa';
        } else {
            return 'action';
        }
    }

    static fromType(pipelineElement: PipelineElementUnion) {
        if (pipelineElement instanceof SpDataStream) {
            return PipelineElementType.DataStream;
        } else if (pipelineElement instanceof DataProcessorInvocation) {
            return PipelineElementType.DataProcessor;
        } else {
            return PipelineElementType.DataSink;
        }
    }

    static toType(elementType: PipelineElementType) {
        if (PipelineElementType.DataStream === elementType) {
            return SpDataStream;
        } else if (PipelineElementType.DataProcessor === elementType) {
            return DataProcessorInvocation;
        } else {
            return DataSinkInvocation;
        }
    }

    static toString(pipelineElement: PipelineElementType): string {
        return PipelineElementType[pipelineElement];
    }

    static parse(pipelineElement: string): PipelineElementType {
        return PipelineElementType[pipelineElement];
    }
}
