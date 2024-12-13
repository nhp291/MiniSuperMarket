//package devmagic.Service;
//
//import devmagic.Dto.AccountEmailDTO;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.util.List;
//
//@Service
//public class TidioService {
//
//    private static final Logger logger = LoggerFactory.getLogger(TidioService.class);
//
//    @Value("${tidio.api.url.contacts.batch}")
//    private String contactsBatchUrl;
//
//    @Value("${tidio.public.key}")
//    private String tidioPublicKey;
//
//    @Value("${tidio.private.key}")
//    private String tidioPrivateKey;
//
//    private final RestTemplate restTemplate;
//
//    public TidioService(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//    }
//
//    // Gửi thông tin tất cả người dùng đến Tidio
//    public void sendAllUsersDataToTidio(List<AccountEmailDTO> accounts) {
//        try {
//            // Chuyển danh sách AccountEmailDTO thành JSONArray
//            JSONArray contactsJson = new JSONArray();
//            for (AccountEmailDTO account : accounts) {
//                JSONObject contactJson = new JSONObject();
//                contactJson.put("email", account.getEmail());
//                contactJson.put("name", account.getName()); // Gán name = email
//                contactJson.put("phone", account.getPhoneNumber()); // Thêm phone
//                contactsJson.put(contactJson);
//            }
//
//            // Tạo yêu cầu HTTP với body là contactsJson
//            HttpEntity<String> request = new HttpEntity<>(contactsJson.toString(), createHeaders());
//            ResponseEntity<String> response = restTemplate.exchange(contactsBatchUrl, HttpMethod.POST, request, String.class);
//
//            if (response.getStatusCode().is2xxSuccessful()) {
//                logger.info("Gửi dữ liệu tất cả người dùng đến Tidio thành công.");
//            } else {
//                logger.error("Không thể gửi dữ liệu tất cả người dùng đến Tidio. Phản hồi: {}", response.getBody());
//            }
//        } catch (Exception e) {
//            logger.error("Lỗi khi gửi dữ liệu tất cả người dùng đến Tidio: {}", e.getMessage(), e);
//            throw new RuntimeException("Error sending data to Tidio", e); // Ném lỗi lên tầng trên
//        }
//    }
//
//    // Hàm tạo header dùng chung
//    private HttpHeaders createHeaders() {
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("X-Tidio-Openapi-Client-Id", tidioPublicKey);
//        headers.set("X-Tidio-Openapi-Client-Secret", tidioPrivateKey);
//        headers.set("Content-Type", "application/json");
//        return headers;
//    }
//}
