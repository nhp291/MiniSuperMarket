package devmagic.Controller.Admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/statistics")
public class StatisticalController {

    @GetMapping("/chart-apex")
    public String chartApex(Model model) {
        model.addAttribute("pageTitle", "General Settings");
        model.addAttribute("viewName", "admin/menu/chart-apex");
        return "admin/layout";
    }

    @GetMapping("chat")
    public String chat(Model model) {
        model.addAttribute("pageTitle", "General Settings");
        model.addAttribute("viewName", "admin/menu/chay");
        return "admin/layout";
    }

}
