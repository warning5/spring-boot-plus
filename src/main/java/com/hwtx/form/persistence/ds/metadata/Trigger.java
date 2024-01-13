
package com.hwtx.form.persistence.ds.metadata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Trigger extends BaseMetadata<Trigger> implements Serializable {
    public enum EVENT{
        INSERT, DELETE, UPDATE;
    }
    public enum TIME{
        BEFORE("BEFORE"),
        AFTER("AFTER"),
        INSTEAD ("INSTEAD OF");
        final String sql;
        TIME(String sql){
            this.sql = sql;
        }
        public String sql(){
            return sql;
        }
    }
    private TIME time;
    private List<EVENT> events = new ArrayList<>();
    private boolean each = true; //每行触发发

    public void setEach(boolean each) {
        this.each = each;
    }

    public TIME getTime() {
        if(getmap && null != update){
            return ((Trigger)update).time;
        }
        return time;
    }

    public Trigger setTime(TIME time) {
        if(setmap && null != update){
            ((Trigger)update).time = time;
            return this;
        }
        this.time = time;
        return this;
    }
    public Trigger setTime(String time) {
        if(setmap && null != update){
            ((Trigger)update).setTime(time);
            return this;
        }
        this.time = TIME.valueOf(time);
        return this;
    }

    public List<EVENT> getEvents() {
        if(getmap && null != update){
            return ((Trigger)update).events;
        }
        return events;
    }

    public Trigger addEvent(EVENT ... events) {
        if(setmap && null != update){
            ((Trigger)update).addEvent(events);
            return this;
        }
        for(EVENT event:events){
            this.events.add(event);
        }
        return this;
    }
    public Trigger addEvent(String ... events) {
        if(setmap && null != update){
            ((Trigger)update).addEvent(events);
            return this;
        }
        for(String event:events){
            this.events.add(EVENT.valueOf(event));
        }
        return this;
    }

    public boolean isEach() {
        if(getmap && null != update){
            return ((Trigger)update).each;
        }
        return each;
    }

    public Trigger clone(){
        Trigger copy = super.clone();
        copy.events.addAll(this.events);
        return copy;
    }
}
