package io.geekidea.boot.auth.util;

import io.geekidea.boot.auth.cache.AppLoginCache;
import io.geekidea.boot.auth.service.AppLoginRedisService;
import io.geekidea.boot.auth.vo.AppLoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author geekidea
 * @date 2022/6/26
 **/
@Component
public class AppLoginUtil {

    private static AppLoginRedisService appLoginRedisService;

    public AppLoginUtil(AppLoginRedisService appLoginRedisService) {
        AppLoginUtil.appLoginRedisService = appLoginRedisService;
    }

    /**
     * 根据token从redis中获取登录用户信息
     *
     * @param token
     * @return
     * @throws Exception
     */
    public static AppLoginVo getLoginVo(String token) throws Exception {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        AppLoginVo appLoginVo = appLoginRedisService.getLoginVo(token);
        return appLoginVo;
    }

    /**
     * 从当前线程中获取登录用户信息
     *
     * @return
     */
    public static AppLoginVo getLoginVo() throws Exception {
        return AppLoginCache.get();
    }

    /**
     * 获取登录用户ID
     *
     * @return
     */
    public static Long getUserId() throws Exception {
        AppLoginVo appLoginVo = getLoginVo();
        if (appLoginVo != null) {
            return appLoginVo.getUserId();
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
        AppLoginVo appLoginVo = getLoginVo();
        if (appLoginVo != null) {
            return appLoginVo.getUsername();
        }
        return null;
    }

    /**
     * 获取用户角色ID
     *
     * @return
     */
    public static Long getUserRoleId() throws Exception {
        AppLoginVo appLoginVo = getLoginVo();
        if (appLoginVo != null) {
            return appLoginVo.getUserRoleId();
        }
        return null;
    }

    /**
     * 获取用户角色编码
     *
     * @return
     */
    public static String getUserRoleCode() throws Exception {
        AppLoginVo appLoginVo = getLoginVo();
        if (appLoginVo != null) {
            return appLoginVo.getUserRoleCode();
        }
        return null;
    }

}
