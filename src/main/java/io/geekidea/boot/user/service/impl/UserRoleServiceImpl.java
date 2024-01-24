package io.geekidea.boot.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.geekidea.boot.framework.exception.BusinessException;
import io.geekidea.boot.framework.page.OrderByItem;
import io.geekidea.boot.framework.page.Paging;
import io.geekidea.boot.user.entity.UserRole;
import io.geekidea.boot.user.mapper.UserRoleMapper;
import io.geekidea.boot.user.query.UserRoleQuery;
import io.geekidea.boot.user.service.UserRoleService;
import io.geekidea.boot.user.vo.UserRoleVo;
import io.geekidea.boot.util.PagingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户角色 服务实现类
 *
 * @author geekidea
 * @since 2024-01-06
 */
@Slf4j
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean addUserRole(io.geekidea.boot.demo.dto.UserRoleDto dto) throws Exception {
        UserRole userRole = new UserRole();
        BeanUtils.copyProperties(dto, userRole);
        return save(userRole);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateUserRole(io.geekidea.boot.demo.dto.UserRoleDto dto) throws Exception {
        Long id = dto.getId();
        UserRole userRole = getById(id);
        if (userRole == null) {
            throw new BusinessException("用户角色不存在");
        }
        BeanUtils.copyProperties(dto, userRole);
        return updateById(userRole);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteUserRole(Long id) throws Exception {
        return removeById(id);
    }

    @Override
    public UserRoleVo getUserRoleById(Long id) throws Exception {
        return userRoleMapper.getUserRoleById(id);
    }

    @Override
    public Paging<UserRoleVo> getUserRolePage(UserRoleQuery query) throws Exception {
        PagingUtil.handlePage(query, OrderByItem.desc("id"));
        List<UserRoleVo> list = userRoleMapper.getUserRolePage(query);
        Paging<UserRoleVo> paging = new Paging<>(list);
        return paging;
    }

}
