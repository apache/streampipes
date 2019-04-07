"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
require("reflect-metadata");
var RdfsClass_1 = require("../RdfsClass");
var RdfsProperty_1 = require("../RdfsProperty");
var RdfId_1 = require("../RdfId");
var Person = /** @class */ (function () {
    // constructor();
    function Person(id, label, description, age, heights) {
        this.id = id;
        this.label = label;
        this.description = description;
        this.age = age;
        this.heights = heights;
    }
    __decorate([
        RdfId_1.RdfId
    ], Person.prototype, "id", void 0);
    __decorate([
        RdfsProperty_1.RdfProperty('sp:label')
    ], Person.prototype, "label", void 0);
    __decorate([
        RdfsProperty_1.RdfProperty('sp:description')
    ], Person.prototype, "description", void 0);
    __decorate([
        RdfsProperty_1.RdfProperty('sp:age')
    ], Person.prototype, "age", void 0);
    __decorate([
        RdfsProperty_1.RdfProperty('sp:heights')
    ], Person.prototype, "heights", void 0);
    __decorate([
        RdfsProperty_1.RdfProperty('foaf:friend')
    ], Person.prototype, "friend", void 0);
    Person = __decorate([
        RdfsClass_1.RdfsClass('sp:Person')
    ], Person);
    return Person;
}());
exports.Person = Person;
