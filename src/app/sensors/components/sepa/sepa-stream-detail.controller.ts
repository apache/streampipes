export class SepaStreamDetailController {

    activeStreamTab: any;

    constructor() {
        this.activeStreamTab = "basics";
    }
    
    selectStreamTab(name) {
        this.activeStreamTab = name;
    }

    isStreamTabSelected(name) {
        return this.activeStreamTab == name;
    }

    getStreamActiveTabCss(name) {
        if (name == this.activeStreamTab) return "md-fab md-accent md-mini";
        else return "md-fab md-accent md-mini wizard-inactive";
    }

    addProperty(properties) {
        properties.push({
            "type": "org.streampipes.model.schema.EventPropertyPrimitive",
            "properties": {"runtimeName": "", "runtimeType": "", "domainProperties": []}
        });
    }

    removeProperty(index, properties) {
        properties.splice(index, 1);
    }
}