
package com.hwtx.form.domain.ds.metadata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Function extends BaseMetadata<Function> implements Serializable {
    protected List<Parameter> parameters = new ArrayList<>();
    protected String definition;

    public List<Parameter> getParameters() {
        if(getmap && null != update){
            return update.parameters;
        }
        return parameters;
    }

    public Function setParameters(List<Parameter> parameters) {
        if(setmap && null != update){
            update.definition = definition;
            return this;
        }
        this.parameters = parameters;
        return this;
    }

    public String getDefinition() {
        if(getmap && null != update){
            return update.definition;
        }
        return definition;
    }

    public Function setDefinition(String definition) {
        if(setmap && null != update){
            update.definition = definition;
            return this;
        }
        this.definition = definition;
        return this;
    }


    public Function clone(){
        Function copy = super.clone();
        List<Parameter> pms = new ArrayList<>();
        for(Parameter parameter:parameters){
            pms.add(parameter.clone());
        }
        copy.parameters = pms;

        return copy;
    }
}
