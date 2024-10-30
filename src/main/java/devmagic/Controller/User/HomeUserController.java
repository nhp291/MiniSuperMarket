package devmagic.Controller.User;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/layout")
public class HomeUserController {

    @GetMapping("/Home")
    public String Home(Model model) {return "layout/Home";
    }

    @GetMapping("/Product")
    public String Product(Model model) {
        return "layout/Product";
    }


    @GetMapping("/ProductDetail")
    public String ProductDetail(Model model) {
        return "layout/ProductDetail";
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "user/login";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        return "user/contact";
    }

    @GetMapping("/register")
    public String register(Model model) {
        return "user/register";
    }

    @GetMapping("/info")
    public String info(Model model) {
        return "user/info";
    }
}

