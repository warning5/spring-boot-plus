package io.geekidea.boot.system.service;

import io.geekidea.boot.framework.service.BaseService;
import io.geekidea.boot.system.entity.SysRoleMenu;

/**
 * 角色菜单关系表 服务接口
 *
 * @author geekidea
 * @since 2022-12-26
 */
public interface SysRoleMenuService extends BaseService<SysRoleMenu> {

    /**
     * 根据角色ID删除角色权限关联关系
     *
     * @param roleId
     */
    void deleteSysRoleMenuByRoleId(Long roleId);

}
