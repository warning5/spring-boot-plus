package io.geekidea.boot.auth.cache;

import io.geekidea.boot.auth.vo.LoginRedisAppVo;

/**
 * 在当前线程中缓存token
 * 如果开启多线程需要获取
 *
 * @author geekidea
 * @date 2023/12/7
 **/
public class LoginAppCache {

    /**
     * 当前线程中保存管理后台登录信息
     */
    private static final ThreadLocal<LoginRedisAppVo> APP_LOGIN_CACHE = new ThreadLocal<>();

    /**
     * 设置管理后台登录信息到当前线程中
     *
     * @param loginRedisAppVo
     */
    public static void set(LoginRedisAppVo loginRedisAppVo) {
        APP_LOGIN_CACHE.set(loginRedisAppVo);
    }

    /**
     * 从当前线程获取管理后台登录信息
     *
     * @return
     */
    public static LoginRedisAppVo get() {
        return APP_LOGIN_CACHE.get();
    }

    /**
     * 从当前线程中移除管理后台登录信息
     */
    public static void remove() {
        APP_LOGIN_CACHE.remove();
    }

}
