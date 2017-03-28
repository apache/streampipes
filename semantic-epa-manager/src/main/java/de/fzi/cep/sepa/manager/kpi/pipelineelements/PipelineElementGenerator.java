package de.fzi.cep.sepa.manager.kpi.pipelineelements;

import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.impl.staticproperty.*;

/**
 * Created by riemer on 03.10.2016.
 */
public abstract class PipelineElementGenerator<T extends InvocableSEPAElement, SE extends Settings> {

    protected T pipelineElement;
    protected SE settings;

    public PipelineElementGenerator(T pipelineElement, SE settings) {
        this.pipelineElement = pipelineElement;
        this.settings = settings;
    }

    public abstract T makeInvocationGraph();

    protected boolean hasInternalName(StaticProperty sp, String internalName) {
        return sp.getInternalName().equals(internalName);
    }

    private <EL extends StaticProperty> EL convert(StaticProperty sp, Class<EL> clazz) {
        return clazz.cast(sp);
    }

    protected MappingPropertyUnary mappingPropertyUnary(StaticProperty sp) {
        return convert(sp, MappingPropertyUnary.class);
    }

    protected MappingPropertyNary mappingPropertyNary(StaticProperty sp) {
        return convert(sp, MappingPropertyNary.class);
    }

    protected FreeTextStaticProperty freeTextStaticProperty(StaticProperty sp) {
        return convert(sp, FreeTextStaticProperty.class);
    }

    protected DomainStaticProperty domainStaticProperty(StaticProperty sp) {
        return convert(sp, DomainStaticProperty.class);
    }

    protected OneOfStaticProperty oneOfStaticProperty(StaticProperty sp) {
        return convert(sp, OneOfStaticProperty.class);
    }

    protected AnyStaticProperty anyStaticProperty(AnyStaticProperty sp) {
        return convert(sp, AnyStaticProperty.class);
    }

}
