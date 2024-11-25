//package devmagic.config;
//
//import devmagic.Service.AccountService;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//
//@Component
//public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
//
//    private final AccountService accountService;
//
//    @Autowired
//    public CustomAuthenticationSuccessHandler(AccountService accountService) {
//        this.accountService = accountService;
//    }
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
//        HttpSession session = request.getSession();
//
//        // Lấy accountId từ AccountService
//        String username = authentication.getName();
//        Integer accountId = accountService.findAccountIdByUsername(username);
//
//        session.setAttribute("accountId", accountId);
//
//        // Điều hướng người dùng
//        String role = authentication.getAuthorities().stream()
//                .map(grantedAuthority -> grantedAuthority.getAuthority())
//                .findFirst()
//                .orElse("");
//
//        if ("ROLE_Admin".equals(role)) {
//            response.sendRedirect("/Admin/Home");
//        } else {
//            response.sendRedirect("/layout/Home");
//        }
//    }
//}
//
