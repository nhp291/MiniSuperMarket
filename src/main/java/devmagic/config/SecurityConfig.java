package devmagic.config;

import devmagic.Model.Account;
import devmagic.Model.Role;
import devmagic.Reponsitory.AccountRepository;
import devmagic.Reponsitory.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    private DataSource dataSource;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AccountRepository accountRepository;


    /**
     * Bean mã hóa mật khẩu sử dụng Pbkdf2 với cấu hình mạnh hơn.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        String secret = "my-strong-secret"; // Secret key để tăng độ an toàn
        int iterationCount = 500;        // Tăng số vòng lặp để tăng độ mạnh
        int hashWidth = 256;               // Độ dài mã hóa
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
                        .requestMatchers("/user/login", "/layout/**", "/Signin/google", "/Signin/facebook", "/product/**", "/user/**", "/user/register", "/css/**", "/js/**", "/Image/**").permitAll()
                        .requestMatchers("/Admin/**").hasRole("Admin")
                        .requestMatchers("/Password/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/user/login")
                        .loginProcessingUrl("/login")
                        .successHandler(authenticationSuccessHandler())
                        .failureHandler((request, response, exception) -> {
                            request.setAttribute("error", "Tên đăng nhập hoặc mật khẩu không chính xác!");
                            request.getRequestDispatcher("/user/login").forward(request, response);
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/layout/Home")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }


    //Xử lý thành công đăng nhập, chuyển hướng theo vai trò của người dùng và lưu accountId vào session.
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            String username = authentication.getName(); // Lấy username từ authentication
            Account account = getAccountByUsername(username); // Lấy Account từ DB

            if (account != null) {
                // Lấy vai trò từ authentication
                String roleName = authentication.getAuthorities().stream()
                        .map(grantedAuthority -> grantedAuthority.getAuthority())
                        .findFirst()
                        .orElse("");

                // Tìm Role từ database
                Role role = roleRepository.findByRoleName(roleName.replace("ROLE_", ""))
                        .orElseThrow(() -> new IllegalArgumentException("Vai trò không hợp lệ: " + roleName));

                // Gán role vào account
                account.setRole(role);

                // Lưu thông tin vào session và chuyển hướng dựa trên vai trò
                if ("ROLE_Admin".equals(roleName)) {
                    request.getSession().setAttribute("Admin", account);
                    request.getSession().setAttribute("accountId", account.getAccountId());
                    System.out.println("Lưu Admin vào session: " + account.getUsername());
                    response.sendRedirect("/Admin/Home");
                } else if ("ROLE_User".equals(roleName)) {
                    request.getSession().setAttribute("User", account);
                    request.getSession().setAttribute("accountId", account.getAccountId());
                    System.out.println("Lưu User vào session: " + account.getUsername());
                    response.sendRedirect("/layout/Home");
                } else {
                    System.out.println("Vai trò không hợp lệ: " + roleName);
                    response.sendRedirect("/user/login?error=invalid-role");
                }
            } else {
                System.out.println("Không tìm thấy tài khoản với username: " + username);
                response.sendRedirect("/user/login?error=true");
            }
        };
    }

    // Lấy thông tin Account từ DB
    private Account getAccountByUsername(String username) {
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản với username: " + username));
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