package io.geekidea.boot.auth.interceptor;

import io.geekidea.boot.auth.annotation.AppUserRole;
import io.geekidea.boot.auth.annotation.Login;
import io.geekidea.boot.auth.cache.AppLoginCache;
import io.geekidea.boot.auth.util.AppLoginUtil;
import io.geekidea.boot.auth.util.TokenUtil;
import io.geekidea.boot.auth.vo.AppLoginVo;
import io.geekidea.boot.framework.exception.AuthException;
import io.geekidea.boot.framework.exception.LoginTokenException;
import io.geekidea.boot.framework.interceptor.BaseExcludeMethodInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author geekidea
 * @date 2022/6/26
 **/
@Slf4j
public class AppLoginInterceptor extends BaseExcludeMethodInterceptor {

    @Override
    protected boolean preHandleMethod(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {
        // 获取token
        String token = TokenUtil.getToken();
        AppLoginVo appLoginVo = null;
        if (StringUtils.isNotBlank(token)) {
            // 获取登录用户信息
            appLoginVo = AppLoginUtil.getLoginVo(token);
            if (appLoginVo != null) {
                // 将APP移动端的登录信息保存到当前线程中
                AppLoginCache.set(appLoginVo);
            }
        }
        // 如果不存在@Login注解，则跳过
        Login loginAnnotation = getLoginAnnotation(handlerMethod);
        if (loginAnnotation == null) {
            return true;
        }
        // 登录校验
        if (StringUtils.isBlank(token)) {
            throw new LoginTokenException("请登录后再操作");
        }
        // 校验登录用户信息
        if (appLoginVo == null) {
            throw new LoginTokenException("登录已过期或登录信息不存在，请重新登录");
        }
        // 校验用户角色
        AppUserRole appUserRoleAnnotation = getAppUserRoleAnnotation(handlerMethod);
        if (appUserRoleAnnotation != null) {
            String[] values = appUserRoleAnnotation.value();
            if (ArrayUtils.isNotEmpty(values)) {
                Long userRoleId = AppLoginUtil.getUserRoleId();
                String userRoleCode = AppLoginUtil.getUserRoleCode();
                // 如果方法上的角色ID或角色编码与当前登录用户一致，则放行，否则，跳过
                if (ArrayUtils.contains(values, userRoleId) || ArrayUtils.contains(values, userRoleCode)) {
                    return true;
                } else {
                    throw new AuthException("当前用户没有权限访问");
                }
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        AppLoginCache.remove();
    }

}
