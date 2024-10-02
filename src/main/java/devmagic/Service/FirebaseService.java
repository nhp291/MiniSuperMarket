package devmagic.Service;

import devmagic.config.FirebaseConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class FirebaseService {
    @Autowired
    private FirebaseConfig firebaseConfig;

    @Autowired
    private RestTemplate restTemplate;

    public String uploadFile(MultipartFile file) throws Exception {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        String firebaseApiUrl = firebaseConfig.getFirebaseApiUrl() + "/o?name=" + fileName;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set("Authorization", "Bearer " + firebaseConfig.getFirebaseApiKey());

        HttpEntity<ByteArrayResource> requestEntity = new HttpEntity<>(new ByteArrayResource(file.getBytes()), headers);

        ResponseEntity<Map> response = restTemplate.exchange(firebaseApiUrl, HttpMethod.POST, requestEntity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, String> body = response.getBody();
            return body != null ? body.get("mediaLink") : null;
        } else {
            throw new Exception("Error uploading file to Firebase: " + response.getStatusCode());
        }
    }
}

