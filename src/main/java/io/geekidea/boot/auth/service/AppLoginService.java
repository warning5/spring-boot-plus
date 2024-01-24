package io.geekidea.boot.auth.service;

import io.geekidea.boot.auth.dto.AppAccountLoginDto;
import io.geekidea.boot.auth.dto.AppLoginDto;
import io.geekidea.boot.auth.vo.AppLoginVo;
import io.geekidea.boot.auth.vo.LoginTokenVo;
import io.geekidea.boot.user.entity.User;

import java.util.Date;

/**
 * @author geekidea
 * @date 2022/7/5
 **/
public interface AppLoginService {

    /**
     * APP小程序登录
     *
     * @param dto
     * @return
     * @throws Exception
     */
    LoginTokenVo login(AppLoginDto dto) throws Exception;

    /**
     * APP账号密码登录
     *
     * @param dto
     * @return
     * @throws Exception
     */
    LoginTokenVo accountLogin(AppAccountLoginDto dto) throws Exception;

    /**
     * APP登录
     *
     * @param user
     * @return
     * @throws Exception
     */
    LoginTokenVo login(User user) throws Exception;

    /**
     * 刷新登录信息
     *
     * @param user
     * @param token
     * @param lastLoginTime
     * @return
     * @throws Exception
     */
    AppLoginVo refreshLoginInfo(User user, String token, Date lastLoginTime) throws Exception;

    /**
     * 获取登录用户信息
     *
     * @return
     * @throws Exception
     */
    AppLoginVo getLoginUserInfo() throws Exception;

    /**
     * 登出
     *
     * @throws Exception
     */
    void logout() throws Exception;

}
