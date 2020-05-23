export function modify_bin(data_body,name,depth,height,width){
    if(name != null){
        data_body.name = name
    }
    if(depth != null){
        data_body.size.depth = depth
    }
    if(height != null){
        data_body.size.height = height
    }
    if(width != null){
        data_body.size.width = width
    }
    return data_body
}

export function add_item(data_body,item){
    data_body.items.push(item)
    return data_body
}

export function remove_item(data_body,item){
    const index = data_body.items.indexOf(item);
    if (index > -1) {
        data_body.items.splice(index, 1);
    }
    return data_body
}

export function create_item(index,name,position_x,position_y,position_z,depth,height,width,type){
    var item = {
        "index": index,
        "name": name,
        "position": {
        "x": position_x,
        "y": position_y,
        "z": position_z
        },
        "size": {
        "depth": depth,
        "height": height,
        "width": width
        },
        "type": type
    }
    return item
}

export function intialize_default(){
    var renderer_data_body = {
        "bin": {
            "name": "euro_pallet",
            "size": {
            "depth": 80,
            "height": 300,
            "width": 120
  
            }
        },
        "items": [],
  
        "timestamp": new Date
    }
    return renderer_data_body
}