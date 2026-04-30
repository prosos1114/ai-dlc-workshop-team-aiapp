package com.tableorder.core.interceptor;

import com.tableorder.core.security.AuthenticatedUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Component
public class StoreAccessInterceptor implements HandlerInterceptor {

    @Override
    @SuppressWarnings("unchecked")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            return true;
        }

        Map<String, String> pathVariables =
                (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        if (pathVariables != null && pathVariables.containsKey("storeId")) {
            Long requestedStoreId = Long.parseLong(pathVariables.get("storeId"));
            if (!requestedStoreId.equals(user.storeId())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied to this store");
                return false;
            }
        }

        return true;
    }
}
