package devmagic.Service;

import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class FirebaseService {

    private final String FIREBASE_API_URL = "https://firebasestorage.googleapis.com/v0/b/your-project-id/o";

    public String uploadFile(MultipartFile file) {
        try {
            // Mã hóa file thành base64
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());

            // Tạo request body
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("image", base64Image);

            // Gửi request POST
            URL url = new URL(FIREBASE_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = new Gson().toJson(requestBody);
            conn.getOutputStream().write(jsonInputString.getBytes());

            if (conn.getResponseCode() == 200) {
                // Xử lý response để lấy URL
                // ... Trả về URL của hình ảnh
                return "https://firebase-url.com/image-path"; // Thay bằng URL thực tế
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
