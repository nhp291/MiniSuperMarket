package devmagic.config;

import devmagic.Service.AccountService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.sql.DataSource;

@Configuration
public class SecurityConfig {

    @Autowired
    @Lazy
    private AccountService accountService; // Đảm bảo AccountService đã có phương thức truy vấn phù hợp


    /**
     * Bean mã hóa mật khẩu sử dụng Pbkdf2 với cấu hình mạnh hơn.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        String secret = "my-strong-secret";
        int iterationCount = 500;
        int hashWidth = 256;
        return new Pbkdf2PasswordEncoder(secret, iterationCount, hashWidth,
                Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);
    }

    /**
     * Cấu hình chuỗi lọc bảo mật.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/user/login", "/layout/Home", "/user/register", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/Admin/**").hasRole("Admin") // Chỉ Admin mới truy cập được /Admin/
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/user/login")             // Trang đăng nhập tùy chỉnh
                        .loginProcessingUrl("/login")         // URL xử lý đăng nhập
                        .successHandler(authenticationSuccessHandler()) // Xử lý thành công
                        .failureUrl("/user/login?error=true") // Trang lỗi đăng nhập
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")                 // URL xử lý đăng xuất
                        .logoutSuccessUrl("/layout/Home")     // Chuyển hướng tới /layout/Home sau khi đăng xuất
                        .invalidateHttpSession(true)          // Xóa toàn bộ session
                        .clearAuthentication(true)            // Xóa thông tin xác thực
                        .permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable);               // Tắt CSRF (chỉ nên dùng khi cần thiết)

        return http.build();
    }


    /**
     * Xử lý thành công đăng nhập, chuyển hướng theo vai trò của người dùng.
     */
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            HttpSession session = request.getSession();

            // Lấy accountId từ AccountService
            String username = authentication.getName();
            Integer accountId = accountService.findAccountIdByUsername(username);

            session.setAttribute("accountId", accountId);

            // Điều hướng người dùng
            String role = authentication.getAuthorities().stream()
                    .map(grantedAuthority -> grantedAuthority.getAuthority())
                    .findFirst()
                    .orElse("");

            if ("ROLE_Admin".equals(role)) {
                response.sendRedirect("/Admin/Home");
            } else {
                response.sendRedirect("/layout/Home");
            }
        };
    }



    /**
     * Cấu hình AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, DataSource dataSource) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.authenticationProvider(daoAuthenticationProvider(dataSource));
        return authBuilder.build();
    }

    /**
     * Cấu hình DaoAuthenticationProvider để sử dụng DataSource.
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(DataSource dataSource) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService(dataSource));
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Cấu hình UserDetailsService để truy vấn dữ liệu từ database.
     */
    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        JdbcDaoImpl jdbcUserDetailsService = new JdbcDaoImpl();
        jdbcUserDetailsService.setDataSource(dataSource);

        // Query để lấy thông tin người dùng
        jdbcUserDetailsService.setUsersByUsernameQuery(
                "SELECT username, password, enabled FROM account WHERE username = ?");

        // Query để lấy vai trò của người dùng
        jdbcUserDetailsService.setAuthoritiesByUsernameQuery(
                "SELECT a.username, CONCAT('ROLE_', r.role_name) FROM account a " +
                        "JOIN role r ON a.role_id = r.role_id WHERE a.username = ?");

        return jdbcUserDetailsService;
    }
}
