package io.geekidea.boot.framework.interceptor;

import io.geekidea.boot.auth.annotation.AppUserRole;
import io.geekidea.boot.auth.annotation.IgnoreLogin;
import io.geekidea.boot.auth.annotation.Login;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author geekidea
 * @date 2023/12/3
 **/
public abstract class BaseMethodInterceptor implements HandlerInterceptor {

    /**
     * 只处理方法的控制器
     *
     * @param request
     * @param response
     * @param handlerMethod
     * @return
     * @throws Exception
     */
    protected abstract boolean preHandleMethod(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Exception;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            return preHandleMethod(request, response, handlerMethod);
        }
        return true;
    }

    /**
     * 获取方法上和类上的@Login注解
     *
     * @param handlerMethod
     * @return
     */
    protected Login getLoginAnnotation(HandlerMethod handlerMethod) {
        // 从方法上获取登录注解
        Login login = handlerMethod.getMethodAnnotation(Login.class);
        if (login != null) {
            return login;
        }
        // 从类上获取登录注解
        login = handlerMethod.getMethod().getDeclaringClass().getAnnotation(Login.class);
        if (login != null) {
            return login;
        }
        return null;
    }

    /**
     * 获取方法上和类上的@IgnoreLogin注解
     *
     * @param handlerMethod
     * @return
     */
    protected IgnoreLogin getIgnoreLoginAnnotation(HandlerMethod handlerMethod) {
        IgnoreLogin ignoreLogin = handlerMethod.getMethodAnnotation(IgnoreLogin.class);
        if (ignoreLogin != null) {
            return ignoreLogin;
        }
        ignoreLogin = handlerMethod.getMethod().getDeclaringClass().getAnnotation(IgnoreLogin.class);
        if (ignoreLogin != null) {
            return ignoreLogin;
        }
        return null;
    }


    /**
     * 获取方法上和类上的@AppUserRole注解
     *
     * @param handlerMethod
     * @return
     */
    protected AppUserRole getAppUserRoleAnnotation(HandlerMethod handlerMethod) {
        AppUserRole appUserRole = handlerMethod.getMethodAnnotation(AppUserRole.class);
        if (appUserRole != null) {
            return appUserRole;
        }
        appUserRole = handlerMethod.getMethod().getDeclaringClass().getAnnotation(AppUserRole.class);
        if (appUserRole != null) {
            return appUserRole;
        }
        return null;
    }


}
