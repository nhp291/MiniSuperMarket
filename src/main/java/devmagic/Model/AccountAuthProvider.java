package devmagic.Model;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Account_AuthProvider")
public class AccountAuthProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int accountAuthId; // Khóa chính

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account; // Tài khoản

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private AuthProvider provider; // Nhà cung cấp phương thức đăng nhập

    @Column(name = "provider_user_id", nullable = false)
    private String providerUserId; // ID từ nhà cung cấp

}

