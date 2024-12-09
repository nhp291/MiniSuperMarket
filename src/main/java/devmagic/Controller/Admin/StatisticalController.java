package devmagic.Controller.Admin;

import devmagic.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Controller
@RequestMapping("/statistics")
public class StatisticalController {

    @Autowired
    private OrderService ordersService;

    @GetMapping("/chart-apex")
    public String chartApex(Model model) {
        model.addAttribute("pageTitle", "General Settings");
        model.addAttribute("viewName", "admin/menu/chart-apex");
        return "admin/layout";
    }



}
