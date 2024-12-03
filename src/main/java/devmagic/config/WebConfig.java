package devmagic.config;

import devmagic.interceptor.AccountInterceptor;
import devmagic.interceptor.CookieInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AccountInterceptor accountInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AccountInterceptor())
                .addPathPatterns("/**"); // Áp dụng cho tất cả các đường dẫn
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/Image/imageUrl/**")
                .addResourceLocations("classpath:/static/Image/imageUrl/");
        registry.addResourceHandler("/Image/imageProfile/**")
                .addResourceLocations("classpath:/static/Image/imageProfile/");
    }
}


