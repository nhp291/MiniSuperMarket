package devmagic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.api.url}")
    private String firebaseApiUrl;

    @Value("${firebase.api.key}")
    private String firebaseApiKey;

    @Value("${firebase.storage.bucket}")
    private String firebaseStorageBucket;

    // Getter methods for the private variables
    public String getFirebaseApiUrl() {
        return firebaseApiUrl;
    }

    public String getFirebaseApiKey() {
        return firebaseApiKey;
    }

    public String getFirebaseStorageBucket() {
        return firebaseStorageBucket;
    }
}

