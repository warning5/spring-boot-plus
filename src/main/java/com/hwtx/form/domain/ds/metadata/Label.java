
package com.hwtx.form.domain.ds.metadata;

import java.io.Serializable;

public class Label extends Table<Label> implements Serializable {
    protected String keyword = "Label"            ;
    protected Label update;

    public Label(){
        this(null);
    }
    public Label(String name){
        this(null, name);
    }
    public Label(Schema schema, String table){
        this(null, schema, table);
    }
    public Label(Catalog catalog, Schema schema, String name){
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
