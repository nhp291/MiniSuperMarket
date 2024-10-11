package devmagic.Controller.User;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cart")
public class CartController {
    @GetMapping("/shoppingcart")
    public String shopingCart(Model model) {

        return "cart/shoppingcart"; // trả về view shoppingcart.html
    }

    @GetMapping("/invoice")
    public String invoice(Model model) {
        return "cart/invoice"; // trả về view invoice.html
    }

    @GetMapping("/paymenthistory")
    public String paymentHistory(Model model) {
        return "cart/paymenthistory"; // trả về view paymenthistory.html
    }
}
