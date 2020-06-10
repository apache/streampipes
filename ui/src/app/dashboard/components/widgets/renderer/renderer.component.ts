/*
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

import { Component, OnInit } from '@angular/core';

import {modify_bin,add_item,remove_item,create_item,intialize_default} from './renderer_functions'

declare function convert(packing_plan): any;
declare function render_on_canvas(canvas_id, viewpoint, options_pref, boxes, pallet): any;
declare function scale_pallet(x, y, z): any;



@Component({
  selector: 'app-renderer',
  templateUrl: './renderer.component.html',
  styleUrls: ['./renderer.component.css']
})



export class RendererComponent implements OnInit {

  constructor() { }


  ngOnInit(): void {

    // var data = {
    //   "bin": {
    //     "name": "euro_pallet",
    //     "size": {
    //       "depth": 80,
    //       "height": 300,
    //       "width": 120

    //     }
    //   },
    //   "items": [{
    //     "index": 0,
    //     "name": "KLT_0",
    //     "position": {
    //       "x": 0,
    //       "y": 0,
    //       "z": 0
    //     },
    //     "size": {
    //       "depth": 30,
    //       "height": 27,
    //       "width": 40
    //     },
    //     "type": "KLT"
    //   }, {
    //     "index": 1,
    //     "name": "KLT_1",
    //     "position": {
    //       "x": 0,
    //       "y": 0,
    //       "z": 30
    //     },
    //     "size": {
    //       "depth": 30,
    //       "height": 27,
    //       "width": 40
    //     },
    //     "type": "KLT"
    //   }, {
    //     "index": 2,
    //     "name": "KLT_2",
    //     "position": {
    //       "x": 40,
    //       "y": 0,
    //       "z": 0
    //     },
    //     "size": {
    //       "depth": 30,
    //       "height": 27,
    //       "width": 40
    //     },
    //     "type": "KLT"
    //   }, {
    //     "index": 3,
    //     "name": "KLT_3",
    //     "position": {
    //       "x": 40,
    //       "y": 0,
    //       "z": 30
    //     },
    //     "size": {
    //       "depth": 30,
    //       "height": 27,
    //       "width": 40
    //     },
    //     "type": "KLT"
    //   }, {
    //     "index": 4,
    //     "name": "KLT_4",
    //     "position": {
    //       "x": 80,
    //       "y": 0,
    //       "z": 0
    //     },
    //     "size": {
    //       "depth": 30,
    //       "height": 27,
    //       "width": 40
    //     },
    //     "type": "KLT"
    //   }, {
    //     "index": 5,
    //     "name": "Red_Box_0",
    //     "position": {
    //       "x": 0,
    //       "y": 27,
    //       "z": 0
    //     },
    //     "size": {
    //       "depth": 39,
    //       "height": 31,
    //       "width": 48
    //     },
    //     "type": "RedBox"
    //   }, {
    //     "index": 6,
    //     "name": "Red_Box_1",
    //     "position": {
    //       "x": 48,
    //       "y": 27,
    //       "z": 0
    //     },
    //     "size": {
    //       "depth": 39,
    //       "height": 31,
    //       "width": 48
    //     },
    //     "type": "RedBox"
    //   }, {
    //     "index": 7,
    //     "name": "Red_Box_2",
    //     "position": {
    //       "x": 0,
    //       "y": 58,
    //       "z": 0
    //     },
    //     "size": {
    //       "depth": 39,
    //       "height": 31,
    //       "width": 48
    //     },
    //     "type": "RedBox"
    //   }, {
    //     "index": 8,
    //     "name": "Red_Box_3",
    //     "position": {
    //       "x": 48,
    //       "y": 58,
    //       "z": 0
    //     },
    //     "size": {
    //       "depth": 39,
    //       "height": 31,
    //       "width": 48
    //     },
    //     "type": "RedBox"
    //   }, {
    //     "index": 9,
    //     "name": "Red_Box_4",
    //     "position": {
    //       "x": 0,
    //       "y": 89,
    //       "z": 0
    //     },
    //     "size": {
    //       "depth": 39,
    //       "height": 31,
    //       "width": 48
    //     },
    //     "type": "RedBox"
    //   }, {
    //     "index": 10,
    //     "name": "Cardboard_0",
    //     "position": {
    //       "x": 48,
    //       "y": 89,
    //       "z": 0
    //     },
    //     "size": {
    //       "depth": 28,
    //       "height": 33,
    //       "width": 48
    //     },
    //     "type": "Cardboard"
    //   }, {
    //     "index": 11,
    //     "name": "Cardboard_1",
    //     "position": {
    //       "x": 0,
    //       "y": 120,
    //       "z": 0
    //     },
    //     "size": {
    //       "depth": 28,
    //       "height": 33,
    //       "width": 48
    //     },
    //     "type": "Cardboard"
    //   }, {
    //     "index": 12,
    //     "name": "Cardboard_2",
    //     "position": {
    //       "x": 48,
    //       "y": 122,
    //       "z": 0
    //     },
    //     "size": {
    //       "depth": 28,
    //       "height": 33,
    //       "width": 48
    //     },
    //     "type": "Cardboard"
    //   }, {
    //     "index": 13,
    //     "name": "Cardboard_3",
    //     "position": {
    //       "x": 0,
    //       "y": 153,
    //       "z": 0
    //     },
    //     "size": {
    //       "depth": 28,
    //       "height": 33,
    //       "width": 48
    //     },
    //     "type": "Cardboard"
    //   }, {
    //     "index": 14,
    //     "name": "Cardboard_4",
    //     "position": {
    //       "x": 48,
    //       "y": 155,
    //       "z": 0
    //     },
    //     "size": {
    //       "depth": 28,
    //       "height": 33,
    //       "width": 48
    //     },
    //     "type": "Cardboard"
    //   }
    //   ]
    // }
    var data = intialize_default()
    var item = {
          "index": 2,
          "name": "KLT_2",
          "position": {
            "x": 40,
            "y": 0,
            "z": 0
          },
          "size": {
            "depth": 30,
            "height": 27,
            "width": 40
          },
          "type": "Cardboard"
        }

        // TODO uncomment next line
    data = add_item(data,item)

    var boxes = convert(data);
    var pallet = scale_pallet(data.bin.size.width, data.bin.size.width / 8, data.bin.size.depth);



    render_on_canvas('canvas', 'front', {width: 640, height: 480}, boxes, pallet);
    /*render_on_canvas('view1', 'top', {width: 640, height: 480}, boxes, pallet);
    render_on_canvas('view2', 'side', {width: 640, height: 480}, boxes, pallet);
    render_on_canvas('view3', 'rotate', {width: 640, height: 480}, boxes, pallet);
    */


  }



}
