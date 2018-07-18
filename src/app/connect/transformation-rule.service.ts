import {Injectable} from '@angular/core';
import {EventSchema} from './schema-editor/model/EventSchema';
import {TransformationRuleDescription} from './model/rules/TransformationRuleDescription';
import {Logger} from '../shared/logger/default-log.service';
import {RenameRuleDescription} from './model/rules/RenameRuleDescription';
import {EventProperty} from './schema-editor/model/EventProperty';
import {EventPropertyPrimitive} from './schema-editor/model/EventPropertyPrimitive';
import {EventPropertyNested} from './schema-editor/model/EventPropertyNested';
import {AddNestedRuleDescription} from './model/rules/AddNestedRuleDescription';
import {k} from '@angular/core/src/render3';
import {MoveRuleDescription} from './model/rules/MoveRuleDesctiption';
import {DeleteRuleDescription} from './model/rules/DeleteRuleDescription';

@Injectable()
export class TransformationRuleService {

    private oldEventSchema: EventSchema = null;
    private newEventSchema: EventSchema = null;

    constructor(public logger: Logger) {}

    public setOldEventSchema(oldEventSchema: EventSchema) {
        this.oldEventSchema = oldEventSchema;
    }

    public setNewEventSchema(newEventSchema: EventSchema) {
        this.newEventSchema = newEventSchema;
    }

    public getTransformationRuleDescriptions(): TransformationRuleDescription[] {
        if (this.oldEventSchema == null || this.newEventSchema == null) {
            this.logger.error("Old and new schema must be defined")
        }

        var transformationRuleDescription: TransformationRuleDescription[] = [];

        // Rename
        transformationRuleDescription = transformationRuleDescription.concat(this.getRenameRules(
            this.newEventSchema.eventProperties, this.oldEventSchema, this.newEventSchema));


        // Create Nested
        transformationRuleDescription = transformationRuleDescription.concat(this.getCreateNestedRules(
            this.newEventSchema.eventProperties, this.oldEventSchema, this.newEventSchema));

        // Move
        transformationRuleDescription = transformationRuleDescription.concat(this.getMoveRules(
            this.newEventSchema.eventProperties, this.oldEventSchema, this.newEventSchema));

        // Delete
        transformationRuleDescription = transformationRuleDescription.concat(this.getDeleteRules(
            this.newEventSchema.eventProperties, this.oldEventSchema, this.newEventSchema));


        return transformationRuleDescription;
    }

    public getMoveRules(newEventProperties: EventProperty[],
                        oldEventSchema: EventSchema,
                        newEventSchema: EventSchema): MoveRuleDescription[] {

        var result: MoveRuleDescription[] = [];

        for (let eventProperty of newEventProperties) {

            if (eventProperty instanceof EventPropertyNested) {

                const tmpResults: MoveRuleDescription[] = this.getMoveRules((<EventPropertyNested> eventProperty).eventProperties, oldEventSchema, newEventSchema);
                result = result.concat(tmpResults);

            }
            const keyOld: string = this.getCompleteRuntimeNameKey(oldEventSchema.eventProperties, eventProperty.id);
            const keyNew: string = this.getCompleteRuntimeNameKey(newEventSchema.eventProperties, eventProperty.id);


            // get prefix
            if (keyOld && keyNew) {

                const keyOldPrefix: string = keyOld.substr(0, keyOld.lastIndexOf("."));
                const keyNewPrefix: string = keyNew.substr(0, keyNew.lastIndexOf("."));

                if (keyOldPrefix != keyNewPrefix) {

                    // old key is equals old route and new name
                    var keyOfOldValue = "";
                    if (keyOldPrefix === "") {
                        keyOfOldValue = keyNew.substr(keyNew.lastIndexOf(".") + 1, keyNew.length)
                    } else {
                        keyOfOldValue = keyOldPrefix + keyNew.substr(keyNew.lastIndexOf("."), keyNew.length);
                    }
                    console.log("keyyy " + keyOfOldValue);
                    result.push(new MoveRuleDescription(keyOfOldValue, keyNewPrefix));
                }
            }

        }

        return result;
    }

    public getCreateNestedRules(newEventProperties: EventProperty[],
                                oldEventSchema: EventSchema,
                                newEventSchema: EventSchema): AddNestedRuleDescription[] {


        var allNewIds: string[] = this.getAllIds(newEventSchema.eventProperties);
        var allOldIds: string[] = this.getAllIds(oldEventSchema.eventProperties);

        const result: AddNestedRuleDescription[] = [];
        for (let id of allNewIds) {

            if (allOldIds.indexOf(id) === -1) {
                const key = this.getCompleteRuntimeNameKey(newEventSchema.eventProperties, id);
                result.push(new AddNestedRuleDescription(key));
            }
        }

        return result;
    }


    public getRenameRules(newEventProperties: EventProperty[],
                          oldEventSchema: EventSchema,
                          newEventSchema: EventSchema): RenameRuleDescription[] {

        var result: RenameRuleDescription[] = [];

        for (let eventProperty of newEventProperties) {
            const keyOld = this.getCompleteRuntimeNameKey(oldEventSchema.eventProperties, eventProperty.id);
            const keyNew = this.getCompleteRuntimeNameKey(newEventSchema.eventProperties, eventProperty.id);

            result.push(new RenameRuleDescription(keyOld, keyNew));
            if (eventProperty instanceof EventPropertyNested) {

                const tmpResults: RenameRuleDescription[] = this.getRenameRules((<EventPropertyNested> eventProperty).eventProperties, oldEventSchema, newEventSchema);
                result = result.concat(tmpResults);

            }
        }

        var filteredResult: RenameRuleDescription[] = [];
        for (let res of result) {
            if (this.getRuntimeNameKey(res.newRuntimeKey) != this.getRuntimeNameKey(res.oldRuntimeKey) && res.oldRuntimeKey) {
                filteredResult.push(res);
            }
        }

        return filteredResult;
    }

    public getDeleteRules(newEventProperties: EventProperty[],
                          oldEventSchema: EventSchema,
                          newEventSchema: EventSchema): DeleteRuleDescription[] {

        var resultKeys: string[] = [];

        var allNewIds: string[] = this.getAllIds(newEventSchema.eventProperties);
        var allOldIds: string[] = this.getAllIds(oldEventSchema.eventProperties);

        for (let id of allOldIds) {
            // if not in new ids create delete rule
            if (allNewIds.indexOf(id) == -1) {
                const key = this.getCompleteRuntimeNameKey(oldEventSchema.eventProperties, id);
                resultKeys.push(key);
            }
        }

        var uniqEs6 = (arrArg) => {
            return arrArg.filter((elem, pos, arr) => {
                var r = true;
                for (let a of arr) {
                    if (elem.startsWith(a) && a != elem) {
                        r = false;
                    }
                }

                return r;
            });
        };

        resultKeys = uniqEs6(resultKeys);

        var resultRules: DeleteRuleDescription[] = [];
        for (let key of resultKeys) {
            resultRules.push(new DeleteRuleDescription(key));
        }

        return resultRules;
    }


    public getCompleteRuntimeNameKey(eventProperties: EventProperty[], id: string): string {
        var result: string = '';

        for (let eventProperty of eventProperties) {

            if (eventProperty.id === id) {
                return eventProperty.getRuntimeName();
            } else {
                if (eventProperty instanceof EventPropertyNested) {
                    var methodResult = this.getCompleteRuntimeNameKey((<EventPropertyNested> eventProperty).eventProperties, id);
                    if (methodResult != null) {
                        result = eventProperty.getRuntimeName() + "." + methodResult;
                    }
                }
            }
        }

        if (result == '') {
            return null;
        } else {
            return result;
        }
    }

    public getRuntimeNameKey(completeKey: string): string {
        if (completeKey) {
            const keyElements = completeKey.split(".");

            if (keyElements.length == 0) {
                return completeKey;
            } else {
                return keyElements[keyElements.length - 1];
            }
        }

    }


    public getAllIds(eventProperties: EventProperty[]): string[] {
        var result: string[] = [];

        for (let eventProperty of eventProperties) {
            result.push(eventProperty.id);

            if (eventProperty instanceof EventPropertyNested) {
                result = result.concat(this.getAllIds((<EventPropertyNested> eventProperty).eventProperties));
            }
        }
        return result;
    }


}