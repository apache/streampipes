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

import {jsPlumb, jsPlumbInstance} from "jsplumb";
import {JsplumbBridge} from "./jsplumb-bridge.service";
import {Injectable} from "@angular/core";

@Injectable()
export class JsplumbFactoryService {

  pipelineEditorInstance: jsPlumbInstance;
  pipelinePreviewInstance: jsPlumbInstance;

  pipelineEditorBridge: JsplumbBridge;
  pipelinePreviewBridge: JsplumbBridge;

  constructor() {
    this.pipelineEditorInstance = this.makePipelineEditorInstance();
    this.pipelinePreviewInstance = this.makePipelinePreviewInstance();

    this.pipelineEditorBridge = new JsplumbBridge(this.pipelineEditorInstance);
    this.pipelinePreviewBridge = new JsplumbBridge(this.pipelinePreviewInstance);

    this.prepareJsplumb(this.pipelineEditorInstance);
    this.prepareJsplumb(this.pipelinePreviewInstance);
  }

  getJsplumbBridge(previewConfig: boolean): JsplumbBridge {
    return previewConfig ? this.pipelineEditorBridge : this.pipelinePreviewBridge;
  }

  makePipelineEditorInstance(): jsPlumbInstance {
    return jsPlumb.getInstance();
  }

  makePipelinePreviewInstance(): jsPlumbInstance {
    return jsPlumb.getInstance();
  }

  prepareJsplumb(jsplumbInstance: jsPlumbInstance) {
    jsplumbInstance.registerEndpointTypes({
      "empty": {
        paintStyle: {
          fill: "white",
          stroke: "#9E9E9E",
          strokeWidth: 2,
        }
      },
      "token": {
        paintStyle: {
          fill: "#BDBDBD",
          stroke: "#9E9E9E",
          strokeWidth: 2
        },
        hoverPaintStyle: {
          fill: "#BDBDBD",
          stroke: "#4CAF50",
          strokeWidth: 4,
        }
      },
      "highlight": {
        paintStyle: {
          fill: "white",
          stroke: "#4CAF50",
          strokeWidth: 4
        }
      }
    });
  }
}
