package devmagic.interceptor;

import devmagic.Model.Account;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AccountInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Lấy thông tin 'account' từ session
        HttpSession session = request.getSession();
        Account account = (Account) session.getAttribute("Admin");

        // Nếu tài khoản tồn tại, thêm vào request để các controller có thể truy cập được
        if (account != null) {
            request.setAttribute("account", account);
        }

        // Tiếp tục xử lý yêu cầu
        return true;
    }
}
