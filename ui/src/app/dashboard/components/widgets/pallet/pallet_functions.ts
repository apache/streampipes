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

export function modify_bin(data_body, name, depth, height, width) {
    if (name != null) {
        data_body.name = name;
    }
    if (depth != null) {
        data_body.size.depth = depth;
    }
    if (height != null) {
        data_body.size.height = height;
    }
    if (width != null) {
        data_body.size.width = width;
    }
    return data_body;
}

export function add_items(data_body, items) {
    data_body.items = items;
    return data_body;
}

export function remove_item(data_body, item) {
    const index = data_body.items.indexOf(item);
    if (index > -1) {
        data_body.items.splice(index, 1);
    }
    return data_body;
}

export function create_item(index, name, position_x, position_y, position_z, depth, height, width, type) {
    const item = {
        'index': index,
        'name': name,
        'position': {
        'x': position_x,
        'y': position_y,
        'z': position_z
        },
        'size': {
        'depth': depth,
        'height': height,
        'width': width
        },
        'type': type
    };
    return item;
}

export function intialize_default() {
    const renderer_data_body = {
        'bin': {
            'name': 'euro_pallet',
            'size': {
            'depth': 80,
            'height': 300,
            'width': 120

            }
        },
        'items': [],

        'timestamp': new Date
    };
    return renderer_data_body;
}
