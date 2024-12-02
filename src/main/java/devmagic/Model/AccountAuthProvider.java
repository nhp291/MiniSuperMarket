package devmagic.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Account_AuthProvider")
public class AccountAuthProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private AuthProviders provider;

    @ManyToOne
    @JoinColumn(name = "provider_user_id")
    private AuthProviders provider_user;

    @Column(name = "provider_token")
    private String providerToken;
}
