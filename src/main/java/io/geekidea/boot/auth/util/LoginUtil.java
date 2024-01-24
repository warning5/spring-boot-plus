package io.geekidea.boot.auth.util;

import io.geekidea.boot.auth.cache.LoginCache;
import io.geekidea.boot.auth.service.LoginRedisService;
import io.geekidea.boot.auth.vo.LoginVo;
import io.geekidea.boot.framework.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author geekidea
 * @date 2022/6/26
 **/
@Component
public class LoginUtil {

    private static LoginRedisService loginRedisService;

    public LoginUtil(LoginRedisService loginRedisService) {
        LoginUtil.loginRedisService = loginRedisService;
    }

    /**
     * 根据token从redis中获取登录用户信息
     *
     * @param token
     * @return
     * @throws Exception
     */
    public static LoginVo getLoginVo(String token) throws Exception {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        LoginVo loginVo = loginRedisService.getLoginVo(token);
        return loginVo;
    }

    /**
     * 从当前线程中获取登录用户信息
     *
     * @return
     */
    public static LoginVo getLoginVo() throws Exception {
        return LoginCache.get();
    }

    /**
     * 获取登录信息
     *
     * @return
     */
    public static List<String> getPermissions() throws Exception {
        LoginVo loginVo = getLoginVo();
        if (loginVo != null) {
            return loginVo.getPermissions();
        }
        return null;
    }

    /**
     * 获取登录用户ID
     *
     * @return
     */
    public static Long getUserId() throws Exception {
        LoginVo loginVo = getLoginVo();
        if (loginVo != null) {
            Long userId = loginVo.getUserId();
            return userId;
        }
        return null;
    }

    /**
     * 获取登录用户ID字符串
     *
     * @return
     * @throws Exception
     */
    public static String getUserIdString() throws Exception {
        Long userId = getUserId();
        if (userId == null) {
            return null;
        }
        return userId.toString();
    }

    /**
     * 获取登录用户名
     *
     * @return
     */
    public static String getUsername() throws Exception {
        LoginVo loginVo = getLoginVo();
        if (loginVo != null) {
            return loginVo.getUsername();
        }
        return null;
    }

    /**
     * 获取登录角色ID
     *
     * @return
     */
    public static Long getRoleId() throws Exception {
        LoginVo loginVo = getLoginVo();
        if (loginVo != null) {
            return loginVo.getRoleId();
        }
        return null;
    }

    /**
     * 判断是否是管理员
     *
     * @return
     * @throws Exception
     */
    public static boolean isAdmin() throws Exception {
        LoginVo loginVo = getLoginVo();
        if (loginVo != null) {
            return loginVo.isAdmin();
        }
        return false;
    }

    /**
     * 判断不是管理员
     *
     * @return
     * @throws Exception
     */
    public static boolean isNotAdmin() throws Exception {
        return !isAdmin();
    }

    /**
     * 检查是否是管理员
     */
    public static void checkAdmin() throws Exception {
        boolean admin = isAdmin();
        if (!admin) {
            throw new BusinessException("不是管理员，无权限" );
        }
    }

}
