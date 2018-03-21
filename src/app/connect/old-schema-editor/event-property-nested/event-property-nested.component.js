"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
var core_1 = require("@angular/core");
var EventProperty_1 = require("../model/EventProperty");
var EventPropertyNested_1 = require("../model/EventPropertyNested");
var EventPropertyNestedComponent = (function () {
    function EventPropertyNestedComponent() {
        this.eventProperties = new Array(0);
        this.delete = new core_1.EventEmitter();
    }
    EventPropertyNestedComponent.prototype.ngOnInit = function () {
        this.property.propertyID = this.index;
    };
    EventPropertyNestedComponent.prototype.ngDoCheck = function () {
        this.property.propertyID = this.index;
    };
    EventPropertyNestedComponent.prototype.OnClickDeleteProperty = function () {
        this.delete.emit(this.property);
    };
    EventPropertyNestedComponent.prototype.deleteProperty = function (property) {
        var toDelete = this.eventProperties.indexOf(property);
        this.eventProperties.splice(toDelete, 1);
    };
    EventPropertyNestedComponent.prototype.addPrimitiveProperty = function () {
        this.eventProperties.push(new EventProperty_1.EventProperty());
    };
    EventPropertyNestedComponent.prototype.addNestedProperty = function () {
        this.eventProperties.push(new EventPropertyNested_1.EventPropertyNested());
    };
    EventPropertyNestedComponent.prototype.isNested = function (value) {
        return value instanceof EventPropertyNested_1.EventPropertyNested;
    };
    return EventPropertyNestedComponent;
}());
__decorate([
    core_1.Input()
], EventPropertyNestedComponent.prototype, "property", void 0);
__decorate([
    core_1.Input()
], EventPropertyNestedComponent.prototype, "index", void 0);
__decorate([
    core_1.Output()
], EventPropertyNestedComponent.prototype, "delete", void 0);
EventPropertyNestedComponent = __decorate([
    core_1.Component({
        selector: 'app-event-property-nested',
        templateUrl: './event-property-nested.component.html',
        styleUrls: ['./event-property-nested.component.css']
    })
], EventPropertyNestedComponent);
exports.EventPropertyNestedComponent = EventPropertyNestedComponent;
