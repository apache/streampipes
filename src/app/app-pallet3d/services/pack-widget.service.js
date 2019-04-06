import * as THREE from 'three';
import * as THREEx from 'threex-domevents';
import 'three-orbitcontrols';

/**
 * Visualize 3D loading plans on top of ThreeJS
 * @author Felix Brandt <brandt@fzi.de>
 */
var PackWidget = {};

console.log(THREEx)
THREEx(THREE, THREEx)

/**
 * Namespace for helper methods
 */
PackWidget.Support = {
    /**
     * Helper method to traverse configuration options.
     *
     * @param options       The configuration options (object)
     * @param path          The path to the requested option (array[string])
     * @param default_value Value to return, if the option is not found
     *
     * @return The value of the configuration option (object)
     */
    getOption: function (options, path, default_value)
    {
        return path.reduce((obj, key) => (obj && typeof obj[key] !== 'undefined') ? obj[key] : default_value, options);
    }
};

/**
 * Scene class with support for cascading loading plans
 *
 * @param options Scene configuration (object)
 */
PackWidget.Scene = function (options)
{
    THREE.Scene.call(this);
    this.options = options;

    /**
     * Get configuration option of this scene.
     * See PackWidget.Support.getOption()
     */
    this.getOption = function (path, default_value)
    {
        return PackWidget.Support.getOption(this.options, path, default_value);
    }

    /**
     * Determine box size from getter method or property
     *
     * @return THREE.Vector3
     */
    this.getBoxSize = function (box)
    {
        if (box.getSize !== undefined) {
            return box.getSize();
        }

        if (box.size !== undefined) {
            return new THREE.Vector3().add(box.size);
        }

        return new THREE.Vector3(1, 1, 1);
    }

    /**
     * Determine box position from getter method or property
     *
     * @return THREE.Vector3
     */
    this.getBoxPosition = function (box)
    {
        if (box.getPosition !== undefined) {
            return box.getPosition();
        }

        if (box.position !== undefined) {
            if (box.position.x === undefined) box.position.x = 0;
            if (box.position.y === undefined) box.position.y = 0;
            if (box.position.z === undefined) box.position.z = 0;

            return new THREE.Vector3().copy(box.position);
        }

        return new THREE.Vector3();
    }

    /**
     * Determine box rotation from getter method or property
     *
     * @return THREE.Euler
     */
    this.getBoxRotation = function (box)
    {
        if (box.getRotation !== undefined) {
            return box.getRotation();
        }

        if (box.rotation !== undefined) {
            if (box.rotation.x === undefined) box.rotation.x = 0;
            if (box.rotation.y === undefined) box.rotation.y = 0;
            if (box.rotation.z === undefined) box.rotation.z = 0;

            return new THREE.Euler(box.rotation.x, box.rotation.y, box.rotation.z);
        }

        return new THREE.Euler();
    }

    /**
     * Determine sub items of a box from getter method or property
     *
     * @return Array
     */
    this.getBoxItems = function (box)
    {
        if (box.getItems !== undefined) {
            return box.getItems();
        }

        if (box.items !== undefined) {
            return box.items;
        }

        return [];
    }

    /**
     * Create box 3D geometry from shape or dimensions
     *
     * @return THREE.Geometry
     */
    this.createBoxGeometry = function (box)
    {
        if (box.shape !== undefined) {
            return this.createBoxShapeGeometry(box);
        }

        var size = this.getBoxSize(box);

        return new THREE.BoxGeometry(size.x, size.y, size.z);
    }

    /**
     * Create box 3D geometry from extruding a 2D shape.
     *
     * @return THREE.Geometry
     */
    this.createBoxShapeGeometry = function (box)
    {
        var shape = new THREE.Shape();
        shape.moveTo(box.shape[0].x, box.shape[0].y);
        for(var j = 1; j < box.shape.length; j++) {
            shape.lineTo(box.shape[j].x, box.shape[j].y);
        }

        var settings = {
            depth: box.depth,
            bevelEnabled: false
        };

        var geometry = new THREE.ExtrudeGeometry(shape, settings);
        geometry.center();

        return geometry;
    }

    /**
     * Create mesh material with given color
     *
     * @return THREE.Material
     */
    this.createMaterial = function (color)
    {
        return new THREE.MeshLambertMaterial({color: color});
    }

    /**
     * Create 3D mesh for a given box geometry.
     *
     * @return THREE.Object3D
     */
    this.createBoxFaces = function (box, geometry)
    {
        var material = this.mesh_material;
        var all_boxes_color = this.getOption(['color'], null);

        if (typeof all_boxes_color === 'function') {
            material = this.createMaterial(all_boxes_color(box));
        } else if (box.color !== undefined) {
            material = this.createMaterial(box.color);
        }

        return new THREE.Mesh(geometry, material);
    }

    /**
     * Create 3D wireframe for a given box geometry.
     *
     * @return THREE.Object3D
     */
    this.createBoxEdges = function (geometry)
    {
        return new THREE.LineSegments(
            new THREE.EdgesGeometry(geometry),
            this.line_material
        );
    }

    /**
     * Setup callback for DOM event on the given box.
     *
     * @param event_manager Event manager (THREEx.domEvents)
     * @param box           The box configuration (object)
     * @param mesh          The 3D object to bind to (THREE.Object3D)
     * @param method        The event method ('click', 'mouseover', 'mouseout')
     */
    this.createMouseEvent = function (event_manager, box, mesh, method)
    {
        var callback = box[method] !== undefined ? box[method] : this.getOption([method], null);

        if (callback) {
            event_manager.addEventListener(mesh, method, function(event) {
                callback(event.target.userData, event.target);
            });
        }
    }

    /**
     * Setup all DOM events for the given box
     */
    this.createMouseEvents = function (event_manager, box, mesh)
    {
        this.createMouseEvent(event_manager, box, mesh, 'click');
        this.createMouseEvent(event_manager, box, mesh, 'mouseover');
        this.createMouseEvent(event_manager, box, mesh, 'mouseout');
    }

    /**
     * Create 3D object of the given box
     *
     * @param box           The box to render (object)
     * @param geometry      The box geometry (THREE.Geometry)
     * @param event_manager Event manager for box callbacks (THREEx.domEvents)
     * @return THREE.Object3D
     */
    this.createBox3D = function (box, geometry, event_manager)
    {
        var object3d = new THREE.Group();

        if (typeof box.draw === 'function') {
            // draw complete object via callback if present
            object3d.add(box.draw(box, geometry));
        } else {
            // draw object the standard way

            var children_count =  this.getBoxItems(box).length;
            if ((box.solid === undefined && children_count == 0) || box.solid) {
                // draw faces of the object
                box.object3d = this.createBoxFaces(box, geometry);
                box.object3d.userData = box;

                if (event_manager) {
                    // add callbacks to mouse events
                    this.createMouseEvents(event_manager, box, box.object3d);
                }

                object3d.add(box.object3d);
            }

            if (box.border === undefined || box.border) {
                // draw wireframe
                object3d.add(this.createBoxEdges(geometry));
            }

            if (typeof box.draw_extra === 'function') {
                // draw additional elements via callback
                object3d.add(box.draw_extra(box, geometry));
            }
        }

        return object3d;
    }

    /**
     * Render box at defined position and rotation.
     *
     * @param box           The box configuration (object)
     * @param parent_offset start coordinates of the surrounding box (THREE.Vector3)
     * @param event_manager Event manager for box callbacks (THREEx.domEvents)
     * @param object3d      The object to render the box in (THREE.Object3D)
     * @return start coordinates of the box (THREE.Vector3)
     */
    this.renderBox = function (box, parent_offset, event_manager, object3d)
    {
        var geometry = this.createBoxGeometry(box);

        // NOTE: the box will be drawn "around" its position
        // we save the offset to draw contained boxes at the right positions
        geometry.computeBoundingBox();
        var box_offset = new THREE.Vector3().add(geometry.boundingBox.min);

        if (box.visible === undefined || box.visible === true) {
            object3d.add(this.createBox3D(box, geometry, event_manager));
        }

        var rot = this.getBoxRotation(box);
        object3d.rotation.set(rot.x, rot.y, rot.z);

        var pos = this.getBoxPosition(box);
        if (parent_offset !== undefined && !this.getOption(['centric'], false)) {
            var rotated_box_offset = new THREE.Vector3().add(box_offset).applyEuler(rot);
            pos = pos.add(parent_offset).sub(rotated_box_offset);
        }
        object3d.position.set(pos.x, pos.y, pos.z);

        return box_offset;
    }

    /**
     * Render box and all contained boxes
     *
     * @param box           The box configuration (object)
     * @param parent_offset start coordinates of the surrounding box (THREE.Vector3)
     * @param event_manager Event manager for box callbacks (THREEx.domEvents)
     * @return start coordinates of the box (THREE.Object3D)
     */
    this.render = function (box, parent_offset, event_manager)
    {
        var object3d = new THREE.Group();
        var box_offset = this.renderBox(box, parent_offset, event_manager, object3d);

        var items = this.getBoxItems(box);
        if (items.length > 0) {
            object3d.add(this.renderAll(items, box_offset, event_manager));
        }

        return object3d;
    }

    /**
     * Render group of boxes.
     *
     * @param boxes         The boxes (Array)
     * @param parent_offset start coordinates of the surrounding box (THREE.Vector3)
     * @param event_manager Event manager for box callbacks (THREEx.domEvents)
     */
    this.renderAll = function (boxes, parent_offset, event_manager)
    {
        var object3d = new THREE.Group();

        for (var i in boxes) {
            object3d.add(this.render(boxes[i], parent_offset, event_manager));
        }

        return object3d;
    }

    /**
     * Add box to scene.
     *
     * @param box The box configuration (object)
     * @param event_manager Event manager for box callbacks (THREEx.domEvents)
     */
    this.addBox = function (box, event_manager)
    {
        this.add(this.render(box, undefined, event_manager));
    }

    /**
     * Initialize this scene.
     */
    this.init = function ()
    {
        this.mesh_material = this.createMaterial(
            this.getOption(['color'], 0x00ff00)
        );

        this.line_material = new THREE.LineBasicMaterial({
            color: this.getOption(['line_color'], 0x000000)
        });
    }

    this.init();
}

PackWidget.Scene.prototype = Object.create(new THREE.Scene());

/**
 * WebGLRenderer with pre-configured lights, camera and controls.
 *
 * @param scene   The scene to render (THREE.Scene)
 * @param dom     DOM object to append the canvas to
 * @param options Renderer configuration (object)
 */
PackWidget.Renderer = function (scene, dom, options)
{
    THREE.WebGLRenderer.call(this, { alpha: true, antialias: true});
    this.scene = scene;
    this.options = options;

    /**
     * Get configuration option of this scene.
     * See PackWidget.Support.getOption()
     */
    this.getOption = function (path, default_value)
    {
        return PackWidget.Support.getOption(this.options, path, default_value);
    }

    /**
     * Calculate minimum camera distance to fit object size on screen.
     *
     * @param fov    Field of view (numeric)
     * @param size   Object size (numeric)
     * @param border Border around object (percent)
     * @param aspect Aspect ratio w.r.t. fov (numeric)
     * @return numeric
     */
    this.calcMinCameraDistance = function (fov, size, border, aspect)
    {
        var gross_size = size * (1.0 + border * 2.0 / 100.0);
        var distance = gross_size / (2.0 * aspect * Math.tan(((Math.PI * fov)/360.0)));

        return distance;
    }

    /**
     * Fit given plane (x/y centered, at z=0) to screen
     */
    this.fitToScreen = function (width, height, border = 20)
    {
        var fov = this.camera.fov;
        var scene_size = this.getSize();
        var width_distance = this.calcMinCameraDistance(fov, width, border, scene_size.width / scene_size.height);
        var height_distance = this.calcMinCameraDistance(fov, height, border, 1.0);
        var distance = Math.max(width_distance, height_distance);

        this.camera.position.setLength(distance);
    }

    /**
     * Create camera from configuration.
     *
     * @return THREE.camera
     */
    this.setupCamera = function ()
    {
        var size = this.getSize();
        var camera = new THREE.PerspectiveCamera(
            this.getOption(['camera', 'fov'], 45),
            this.getOption(['camera', 'ratio'], size.width / size.height),
            this.getOption(['camera', 'near'], 0.1),
            this.getOption(['camera', 'far'], 10000)
        );

        camera.position.x = this.getOption(['camera', 'x'], 0);
        camera.position.y = this.getOption(['camera', 'y'], 4);
        camera.position.z = this.getOption(['camera', 'z'], 10);

        return camera;
    }

    /**
     * Create light position from configuration.
     *
     * @return THREE.Vector3
     */
    this.getLightVector = function ()
    {
        return new THREE.Vector3(
            this.getOption(['light', 'x'], 0),
            this.getOption(['light', 'y'], 0),
            this.getOption(['light', 'z'], 0)
        );
    }

    /**
     * Create light from configuration.
     *
     * @return THREE.Light
     */
    this.setupLight = function (source)
    {
        var light = new THREE.DirectionalLight (this.getOption(['light', 'color'], 0xFFFFFF), 1);
        light.position.copy(source);

        return light;
    }

    /**
     * Add box to the scene of this renderer.
     *
     * @param box The box configuration (object)
     */
    this.addBox = function (box)
    {
        this.scene.addBox(box, this.domEvents);
    }

    /**
     * Animation loop.
     */
    this.animate = function ()
    {
        // schedule next call of this loop.
        setTimeout (function(renderer){
            requestAnimationFrame(function () { renderer.animate() });
        }, 1000 / this.getOption(['fps'], 60), this);

        // load changes from controls into scene
        //this.controls.update();

        if (this.getOption(['light', 'fixed'], true)) {
            // move light to new camera position
            this.light.position.copy(this.camera.position).add(this.light_position);
        }

        this.render(this.scene, this.camera);
    }

    this.setScene = function (scene)
    {
        scene.add(this.light);
        this.scene = scene;
    }

    /**
     * Initialize this renderer.
     */
    this.init = function ()
    {
        // setup canvas size
        this.setSize(
            this.getOption(['width'], window.innerWidth),
            this.getOption(['height'], window.innerHeight)
        );

        // setup scene with camera and light
        this.camera = this.setupCamera();
        this.light_position = this.getLightVector();
        this.light = this.setupLight(this.light_position);
        this.setScene(scene);

        // setup callbacks
        dom.appendChild(this.domElement);
        this.domEvents = new THREEx.DomEvents(this.camera, this.domElement);


        // setup mouse controls
        this.controls = new THREE.OrbitControls(this.camera, this.domElement);
        if (this.options.controls !== undefined) {
            for (var o in this.options.controls) {
                this.controls[o] = this.options.controls[o];
            }
        }


        this.animate();
    }

    this.init(dom);
}

PackWidget.Renderer.prototype = Object.create(new THREE.WebGLRenderer());
export default PackWidget;
