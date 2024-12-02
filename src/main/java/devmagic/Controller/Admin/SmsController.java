package devmagic.Controller.Admin;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;

@RestController
public class SmsController {

    @GetMapping("/sendSms/{toMobileNo}/{text}")
    public ResponseEntity sendSMS(@PathVariable("toMobileNo") String toMobileNo,@PathVariable("text") String text)
    {

        Twilio.init("ACac8f8a3c6b7176ca76e8bca40ac6d416","fd27058e26430d77100deedbc7ea82a2");

        Message.creator(new PhoneNumber(toMobileNo), new PhoneNumber("+84976260335"), text).create();

        return ResponseEntity.ok("SMS sent successfully!");

    }

    // Find your Account Sid and Token at twilio.com/console
//    public static final String ACCOUNT_SID = "ACac8f8a3c6b7176ca76e8bca40ac6d416";
//    public static final String AUTH_TOKEN = "fd27058e26430d77100deedbc7ea82a2";
//
//    public static void main(String[] args) {
//        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
//        Verification verification = Verification.creator(
//                        "VA6e9851a3c806786457e80abf5a01eb98",
//                        "+84976260335",
//                        "sms")
//                .create();
//        System.out.println(verification.getSid());
//    }

}