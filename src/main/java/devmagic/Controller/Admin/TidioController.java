//package devmagic.Controller.Admin;
//
//import devmagic.Dto.AccountEmailDTO;
//import devmagic.Model.Account;
//import devmagic.Service.AccountService;
//import devmagic.Service.TidioService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@RestController
//public class TidioController {
//
//    private static final Logger logger = LoggerFactory.getLogger(TidioController.class);
//
//    @Autowired
//    private AccountService accountService;
//
//    @Autowired
//    private TidioService tidioService;
//
//    @GetMapping("/sendAllAccountsToTidio")
//    public ResponseEntity<String> sendAllAccountsToTidio() {
//        try {
//            // Lấy danh sách tất cả tài khoản
//            List<Account> allAccounts = accountService.getAllAccounts();
//
//            // Chuyển đổi danh sách Account thành AccountEmailDTO để gửi lên Tidio
//            List<AccountEmailDTO> accountEmailDTOs = allAccounts.stream()
//                    .map(account -> new AccountEmailDTO(account.getEmail(), account.getPhoneNumber()))
//                    .collect(Collectors.toList());
//
//            // Gửi tất cả dữ liệu người dùng lên Tidio
//            tidioService.sendAllUsersDataToTidio(accountEmailDTOs);
//
//            return ResponseEntity.ok("All user data sent to Tidio successfully.");
//        } catch (Exception e) {
//            logger.error("Failed to send user data to Tidio: {}", e.getMessage(), e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Failed to send user data to Tidio");
//        }
//    }
//
//}
