package com.hwtx.form.persistence.ds;

import com.hwtx.form.persistence.ds.metadata.ACTION;

import java.util.ArrayList;

public class SimpleRun extends BasicRun {
    public SimpleRun(DataRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public String getFinalExists(boolean placeholder) {
        return null;
    }

    public String getFinalUpdate() {
        return builder.toString();
    }

    @Override
    public boolean checkValid() {
        return false;
    }

    @Override
    public String log(ACTION.DML action, boolean placeholder) {
        return null;
    }

    public SimpleRun addValue(Object value) {
        RunValue runValue = new RunValue();
        runValue.setValue(value);
        if (null == values) {
            values = new ArrayList<>();
        }
        values.add(runValue);
        return this;
    }
}
