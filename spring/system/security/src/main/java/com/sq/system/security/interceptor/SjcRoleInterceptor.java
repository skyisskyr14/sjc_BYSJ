package com.sq.system.security.interceptor;

import com.sq.system.security.annotation.SjcRoleLimit;
import com.sq.system.security.context.SjcRoleContextHolder;
import com.sq.system.security.context.UserTokenContextHolder;
import com.sq.system.security.model.SjcUserRoleInfo;
import com.sq.system.security.resolver.SjcRoleResolver;
import com.sq.system.usercore.entity.UserEntity;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Set;

@Component
public class SjcRoleInterceptor implements HandlerInterceptor {

    @Resource
    private SjcRoleResolver sjcRoleResolver;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod method)) {
            return true;
        }

        UserEntity user = UserTokenContextHolder.get();
        if (user == null) {
            return deny(response, HttpServletResponse.SC_UNAUTHORIZED, "未登录或登录已过期");
        }

        SjcUserRoleInfo roleInfo = sjcRoleResolver.resolveByUserId(user.getId());
        if (roleInfo == null || roleInfo.getRoleCode() == null || roleInfo.getRoleCode().isBlank()) {
            roleInfo = new SjcUserRoleInfo(0L, "VIEWER", "访客");
        }
        SjcRoleContextHolder.set(roleInfo);

        SjcRoleLimit limit = method.getMethodAnnotation(SjcRoleLimit.class);
        if (limit == null) {
            limit = method.getBeanType().getAnnotation(SjcRoleLimit.class);
        }
        if (limit == null) {
            return true;
        }

        Set<String> allowed = Set.copyOf(Arrays.asList(limit.value()));
        if (allowed.contains(roleInfo.getRoleCode())) {
            return true;
        }
        return deny(response, HttpServletResponse.SC_FORBIDDEN, "当前身份无权执行此操作");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        SjcRoleContextHolder.clear();
    }

    private boolean deny(HttpServletResponse response, int status, String message) throws Exception {
        response.setStatus(status);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write(message);
        return false;
    }
}
