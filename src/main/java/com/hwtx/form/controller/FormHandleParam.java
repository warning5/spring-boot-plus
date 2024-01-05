package com.hwtx.form.controller;

import lombok.Data;

@Data
public class FormHandleParam {
    Long formId;
    String handleAction;
    String updateFromItem;
    String updateToItem;
    String addItem;
}
