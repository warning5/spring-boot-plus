package com.hwtx.form.persistence;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwtx.form.domain.FormDef;
import com.hwtx.form.domain.FormRepo;
import com.hwtx.form.domain.dto.FormDefDto;
import com.hwtx.form.domain.query.FormDefAppQuery;
import com.hwtx.form.domain.query.FormValueQuery;
import com.hwtx.form.domain.vo.FormDefAppVo;
import com.hwtx.form.domain.vo.FormDefVo;
import io.geekidea.boot.framework.exception.BusinessException;
import io.geekidea.boot.framework.page.OrderByItem;
import io.geekidea.boot.framework.page.Paging;
import io.geekidea.boot.util.PagingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 表单定义 服务实现类
 *
 * @author hwtx
 * @since 2023-12-10
 */
@Slf4j
@Repository
public class FormRepoImpl extends ServiceImpl<FormDefMapper, FormDefEntity> implements FormRepo {

    @Resource
    private FormDefMapper formDefMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean addFormDef(FormDefDto dto) throws Exception {
        FormDefEntity formDefEntity = new FormDefEntity();
        BeanUtils.copyProperties(dto, formDefEntity);
        return save(formDefEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateFormDef(FormDefDto dto) throws Exception {
        Long id = dto.getId();
        FormDefEntity formDefEntity = getById(id);
        if (formDefEntity == null) {
            throw new BusinessException("表单定义不存在");
        }
        BeanUtils.copyProperties(dto, formDefEntity);
        return updateById(formDefEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteFormDef(Long id) throws Exception {
        return removeById(id);
    }

    @Override
    public String getFormRawContent(Long id) throws Exception {
        FormDefEntity entity = formDefMapper.getFormDefById(id);
        if (entity != null) {
            return entity.getContent();
        }
        return null;
    }
    @Override
    public FormDef getFormDef(Long id) throws Exception {
        FormDefEntity entity = formDefMapper.getFormDefById(id);
        if (entity != null) {
            FormDef formDef = JSON.parseObject(entity.getContent(), FormDef.class);
            formDef.setFormId(entity.getId().toString());
            return formDef;
        }
        return null;
    }

    @Override
    public Paging<FormDefAppVo> getAppFormDefPage(FormDefAppQuery query) throws Exception {
        PagingUtil.handlePage(query, OrderByItem.desc("id"));
        List<FormDefAppVo> list = formDefMapper.getAppFormDefPage(query);
        return new Paging<>(list);
    }
}
