package devmagic.Model;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "AuthProviders")
public class AuthProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int providerId; // Khóa chính

    @Column(name = "provider_name", nullable = false)
    private String providerName; // Tên nhà cung cấp

}

