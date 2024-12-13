package devmagic.Service;

import devmagic.Model.Account;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@Service
public class TidioService {

    @Value("${tidio.public.key}")
    private String tidioPublicKey;

    @Value("${tidio.private.key}")
    private String tidioPrivateKey;

    private final RestTemplate restTemplate;

    public TidioService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendUserDataToTidio(Account account) {
        String url = "https://api.tidio.com/api/v1/contacts";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Tidio-Openapi-Client-Id", tidioPublicKey);
        headers.set("X-Tidio-Openapi-Client-Secret", tidioPrivateKey);
        headers.set("Content-Type", "application/json");

        // Kiểm tra trạng thái đăng nhập
        if (account.getUsername() == null || account.getUsername().isEmpty()) {
            account.setUsername("Guest");
            account.setEmail(account.getIp());
        }

        HttpEntity<Account> request = new HttpEntity<>(account, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("User data sent to Tidio successfully.");
        } else {
            System.out.println("Failed to send user data to Tidio.");
        }
    }
}