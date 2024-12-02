package devmagic.Utils;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ModelAttribute;
import devmagic.Model.Account;

public class BaseController {

    @ModelAttribute("account")
    public Account populateAccount(HttpSession session) {
        return (Account) session.getAttribute("Admin");
    }
}

