package devmagic.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int accountId; // Khóa chính

    @Column(name = "username", nullable = false)
    private String username; // Tên người dùng

    @Column(name = "password", nullable = false)
    private String password; // Mật khẩu

    @Column(name = "email", nullable = false, unique = true)
    private String email; // Email

    @Column(name = "phone_number")
    private String phoneNumber; // Số điện thoại

    @Column(name = "address")
    private String address; // Địa chỉ

    @Column(name = "image_url")
    private String imageUrl; // Đường dẫn hình ảnh

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role; // Vai trò người dùng

}

