package com.hwtx.form.domain.service;

import com.hwtx.form.controller.FormHandleParam;
import com.hwtx.form.domain.def.FormDef;
import com.hwtx.form.domain.query.FormValueQuery;
import com.hwtx.form.domain.vo.FormData;

import java.util.Map;

public interface FormService {
    String getRawFormDef(Long formId) throws Exception;

    FormDef getFormDef(Long formId) throws Exception;

    void saveFormData(Map<String, String> formData, String user) throws Exception;

    Map<String, String> validateForm(Long formId, Map<String, String> formValues) throws Exception;

    FormData getFormData(FormValueQuery formValueQuery) throws Exception;

    void removeValue(FormValueQuery formValueQuery) throws Exception;

    void handleForm(FormHandleParam formHandleParam) throws Exception;
}
