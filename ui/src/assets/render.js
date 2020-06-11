var pallet = {
  size: { x: 0.44, y: 0.055, z: 0.28},
  border: false,
  color: 0xDBAB78,
  items: [
	{ size: { x: 0.44, y: 0.01, z: 0.049 }, position: { y: 0.045}},
	{ size: { x: 0.44, y: 0.01, z: 0.033 }, position: { y: 0.045, z: 0.066}},
	{ size: { x: 0.44, y: 0.01, z: 0.049 }, position: { y: 0.045, z: 0.115}},
	{ size: { x: 0.44, y: 0.01, z: 0.033 }, position: { y: 0.045, z: 0.179}},
	{ size: { x: 0.44, y: 0.01, z: 0.049 }, position: { y: 0.045, z: 0.231}},
	{ size: { x: 0.049, y: 0.01, z: 0.28 }, position: { y: 0.035}},
	{ size: { x: 0.049, y: 0.01, z: 0.28 }, position: { x: 0.195, y: 0.035}},
	{ size: { x: 0.049, y: 0.01, z: 0.28 }, position: { x: 0.391, y: 0.035}},
	{ size: { x: 0.049, y: 0.025, z: 0.033 }, position: { y: 0.01}},
	{ size: { x: 0.049, y: 0.025, z: 0.033 }, position: { x: 0.195, y: 0.01}},
	{ size: { x: 0.049, y: 0.025, z: 0.033 }, position: { x: 0.391, y: 0.01}},
	{ size: { x: 0.049, y: 0.025, z: 0.033 }, position: { y: 0.01, z: 0.123}},
	{ size: { x: 0.049, y: 0.025, z: 0.033 }, position: { x: 0.195, y: 0.01, z: 0.123}},
	{ size: { x: 0.049, y: 0.025, z: 0.033 }, position: { x: 0.391, y: 0.01, z: 0.123}},
	{ size: { x: 0.049, y: 0.025, z: 0.033 }, position: { y: 0.01, z: 0.247}},
	{ size: { x: 0.049, y: 0.025, z: 0.033 }, position: { x: 0.195, y: 0.01, z: 0.247}},
	{ size: { x: 0.049, y: 0.025, z: 0.033 }, position: { x: 0.391, y: 0.01, z: 0.247}},
	{ size: { x: 0.44, y: 0.01, z: 0.033 }, position: { z: 0.0}},
	{ size: { x: 0.44, y: 0.01, z: 0.033 }, position: { z: 0.123}},
	{ size: { x: 0.44, y: 0.01, z: 0.033 }, position: { z: 0.247}},

	//{ size: { x: 0.16, y: 0.11, z: 0.105 }, position: { x: 0.05, y: 0.055, z: 0.09 }, color: 0xFFCA33 },
	//{ size: { x: 0.129, y: 0.045, z: 0.084 }, position: { x: 0.25, y: 0.055, z: 0.09 }, color: 0xFFCA33 },
	//{ size: { x: 0.135, y: 0.075, z: 0.095 }, position: { x: 0.05, y: 0.165, z: 0.09 }, color: 0xEEEEEE },
	//{ size: { x: 0.104, y: 0.063, z: 0.079 }, position: { x: 0.25, y: 0.10, z: 0.09 }, color: 0xEEEEEE },
  ]
};


function scale_pallet(x, y, z) {
	var new_pallet = {
		size: { x: x, y: y, z: z},
		border: pallet.border,
		color: pallet.color,
		items: new Array()
	}
	scale_x = x / pallet.size.x;
	scale_y = y / pallet.size.y;
	scale_z = z / pallet.size.z;
	for (i = 0; i < pallet.items.length; i++) {
		var cur = pallet.items[i];
		if (cur.position.x == undefined) {
			cur.position.x = 0;
		}
		if (cur.position.y == undefined) {
			cur.position.y = 0;
		}
		if (cur.position.z == undefined) {
			cur.position.z = 0;
		}
		var item = {
			size: { x: scale_x * cur.size.x, y: scale_y * cur.size.y, z: scale_z * cur.size.z },
			//size: {x: cur.size.x, y: cur.size.y, z: cur.size.z},
			//	move to negative coordinates and center
			position: { x: scale_x * cur.position.x, y: scale_y * cur.position.y - y / 2, z: scale_z * cur.position.z},
			//position: {x: cur.position.x, y: cur.position.y, z: cur.position.z}
			color: pallet.color
		}
		new_pallet.items.push(item);
	}
	return new_pallet;
}

function convert(packing_plan) {
	var bin = packing_plan.bin;
	var items = packing_plan.items;

	var boxes = {items: new Array(), bin: bin}

	for (i = 0; i < items.length; i++) {
		var item = items[i];
		var box = {color: 0x000000, size: {x: 0, y: 0, z:0}, position: {x: 0, y:0, z:0}, solid: true};
		switch (item.type) {
			case "KLT":
				box.color = 0x0000FF;
				break;
			case "RedBox":
				box.color = 0xFF0000;
				break;
			case "Cardboard":
				box.color = 0xDBA55C;
				break;
		}
		box.size.x = item.size.width;
		box.size.y = item.size.height;
		box.size.z = item.size.depth;

		// pos == object center

		box.position.x = item.position.x + box.size.x / 2 - (bin.size.width / 2);
		box.position.y = item.position.y + box.size.y / 2;
		box.position.z = item.position.z + box.size.z / 2 - (bin.size.depth / 2);
		boxes.items.push(box);
	}

	return boxes;
}

function render_on_canvas(canvas_id, viewpoint, options_pref, boxes, pallet) {
	var options = {}
	switch(viewpoint) {
		case "top":
			options.camera = {
				fov: 45,
				ratio: 4/3,
				near: 0.1,
				far: 1000,
				x: 0,
				y: boxes.bin.size.height * 3,
				z: 0,
				rotation: { z: 0 }
			  }
			  break;
		case "front":
			options.camera = {
				fov: 45,
				ratio: 4/3,
				near: 0.1,
				far: 1000,
				x: 0,
				y: 0,
				z: boxes.bin.size.depth * 4
			  }
			  break;
		case "side":
			options.camera = {
				fov: 45,
				ratio: 4/3,
				near: 0.1,
				far: 1000,
				x: boxes.bin.size.width * 3,
				y: 0,
				z: 0,
				rotation: { x: 0 }
			  }
			  break;
		case "rotate":
			options.controls = {
				autoRotate: true,
				enablePan: false,
				maxPolarAngle: Math.PI/3,
				minPolarAngle: Math.PI/3
			}
			options.camera = {
				fov: 45,
				ratio: 4/3,
				near: 0.1,
				far: 1000,
				x: boxes.bin.size.width * 2,
				y: boxes.bin.size.height * 2,
				z: boxes.bin.size.depth * 2
			}
			break;
	}
	options.width = options_pref.width;
	options.height = options_pref.height;
	scene = new PackWidget.Scene();
	element = document.getElementById(canvas_id);
	if (element.firstChild) {
		element.removeChild(element.firstChild);
	}
	renderer = new PackWidget.Renderer(scene, document.getElementById(canvas_id), options);
	// document.getElementById(canvas_id)[0].remove();
	for (i = 0; i < boxes.items.length; i++) {
		renderer.addBox(boxes.items[i]);
	}
	// add pallet
	renderer.addBox(pallet);
}
