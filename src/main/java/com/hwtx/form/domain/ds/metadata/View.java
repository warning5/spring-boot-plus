

package com.hwtx.form.domain.ds.metadata;

import java.io.Serializable;

public class View extends Table<View> implements Serializable {
    protected String keyword = "VIEW"            ;
    protected String definition;

    public String getDefinition() {
        if(getmap && null != update){
            return update.definition;
        }
        return definition;
    }

    public View setDefinition(String definition) {
        if(setmap && null != update){
            update.definition = definition;
        }
        this.definition = definition;
        return this;
    }


    public View(){
        this(null);
    }
    public View(String name){
        this(null, name);
    }
    public View(Schema schema, String table){
        this(null, schema, table);
    }
    public View(Catalog catalog, Schema schema, String name){
        this.catalog = catalog;
        this.schema = schema;
        this.name = name;
    }
    public String getKeyword() {
        return keyword;
    }

    public String toString(){
        return this.keyword+":"+name;
    }
}
