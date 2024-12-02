package devmagic.interceptor;

import devmagic.Model.Account;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class AccountInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (modelAndView != null) {
            // Lấy tài khoản từ session
            Object account = request.getSession().getAttribute("User"); // Lấy thông tin User từ session

            if (account != null) {
                // Kiểm tra vai trò của tài khoản
                Account userAccount = (Account) account;
                if ("Admin".equals(userAccount.getRole().getRoleName())) {
                    // Nếu là Admin, hiển thị thông tin tất cả người dùng (hoặc thông tin quản trị viên)
                    modelAndView.addObject("account", account);
                    // Thêm các thông tin khác của người dùng nếu cần thiết
                } else {
                    // Nếu là User, chỉ hiển thị thông tin của người dùng hiện tại
                    modelAndView.addObject("account", account);
                }
            }
        }
    }

}
