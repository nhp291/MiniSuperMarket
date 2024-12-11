//package devmagic.Utils;
//
//import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
//
//public class PasswordHasher {
//    public static void main(String[] args) {
//        // Cấu hình Pbkdf2PasswordEncoder với giá trị mặc định
//        String secret = "my-strong-secret";
//        int iterationCount = 500;
//        int hashWidth = 256;
//        Pbkdf2PasswordEncoder passwordEncoder = new Pbkdf2PasswordEncoder(secret, iterationCount, hashWidth, Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);
//
//        // Danh sách tài khoản: AccountId, Username, Password
//        String[][] accounts = {
//                {"1", "HoaiPhong", "HoaiPhong@123"},
//                {"2", "Phong", "HoaiPhong@123"},
//                {"3", "TuanHuy", "TuanHuy@123"},
//                {"4", "Huy", "TuanHuy@123"},
//                {"5", "HongPhuc", "HongPhuc@123"},
//                {"6", "Phuc", "HongPhuc@123"},
//                {"7", "ThanhPhat", "ThanhPhat@123"},
//                {"8", "Phat", "ThanhPhat@123"},
//        };
//
//        // Tạo và in câu lệnh SQL với mật khẩu đã mã hóa
//        for (String[] account : accounts) {
//            String accountId = account[0];
//            String username = account[1];
//            String rawPassword = account[2];
//            String encodedPassword = passwordEncoder.encode(rawPassword);
//
//            // In câu lệnh SQL với định dạng đúng như bạn yêu cầu
//            System.out.println("UPDATE Account SET password = '" + encodedPassword + "' WHERE account_id = " + accountId + ";");
//        }
//    }
//}
