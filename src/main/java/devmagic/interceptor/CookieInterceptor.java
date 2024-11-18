package devmagic.interceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class CookieInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Lấy cookie từ request
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("username".equals(cookie.getName())) {
                    request.setAttribute("username", cookie.getValue());
                }
                if ("accountId".equals(cookie.getName())) {
                    request.setAttribute("accountId", cookie.getValue());
                }
            }
        }
        return true; // Tiếp tục xử lý request
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            // Thêm thuộc tính vào model để sử dụng trong view
            Object username = request.getAttribute("username");
            Object accountId = request.getAttribute("accountId");

            if (username != null) {
                modelAndView.addObject("username", username);
            }
            if (accountId != null) {
                modelAndView.addObject("accountId", accountId);
            }
        }
    }

}
