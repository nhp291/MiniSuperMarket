package devmagic.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Account")
public class    Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer accountId;

    @Column(name = "username", nullable = false, unique = true)
    @NotEmpty(message = "Tên người dùng không được để trống")
    @Size(min = 3, max = 50, message = "Tên người dùng phải có từ 3 đến 50 ký tự")
    private String username;

    @Lob
    @Column(name = "password", nullable = false)
    @NotEmpty(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;

    @Column(name = "email", nullable = false, unique = true)
    @NotNull(message = "Email không được để trống")
    @Email(message = "Định dạng email không hợp lệ")
    private String email;

    @Column(name = "phone_number")
    @Pattern(regexp = "^(0|\\+84)(\\d{9})$", message = "Số điện thoại không hợp lệ")
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true; // Giá trị mặc định là true

    @Transient
    @NotEmpty(message = "Xác nhận mật khẩu không được để trống")
    private String confirmPassword; // Không lưu vào cơ sở dữ liệu

}
