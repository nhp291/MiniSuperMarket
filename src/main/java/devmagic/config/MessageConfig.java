package devmagic.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class MessageConfig {

    @Bean
    public MessageSource messageSource() {
        // Khởi tạo đối tượng ReloadableResourceBundleMessageSource
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();

        // Cấu hình đường dẫn tới các file messages.properties
        messageSource.setBasename("classpath:messages"); // Đặt tên file messages mà bạn muốn sử dụng
        messageSource.setDefaultEncoding("UTF-8"); // Cấu hình mã hóa UTF-8
        return messageSource;
    }
}