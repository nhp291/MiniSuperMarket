package devmagic.Controller.User;



import devmagic.Model.Product;
import devmagic.Service.ProductSV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeUserController {
    @Autowired
    ProductSV productSV;

    @RequestMapping("/layout/Home")
    public String Home(Model model, @Param("keyword") String keyword, @RequestParam(name = "pageNo",defaultValue = "1") Integer pageNo) {
        Page<Product> list = this.productSV.getall(pageNo);
        List<Product> product2 = this.productSV.finTop6Product();
        Page<Product> list1 = this.productSV.getForAll(pageNo);
        if (keyword != null) {
            list = this.productSV.seachProduct(keyword, pageNo);
            model.addAttribute("keyword", keyword);
        }
        model.addAttribute("totalPage", list.getTotalPages());
        model.addAttribute("currenPage", pageNo);
        model.addAttribute("product", list);
        model.addAttribute("product1", list1);
        model.addAttribute("product2", product2);
        return "layout/Home";
    }

    @RequestMapping("/layout/Product")
    public String Product(Model model) {
        List<Product> list = this.productSV.findAll();
        model.addAttribute("product", list);
        return "layout/Product";
    }


    @RequestMapping("/product/detail/{id}")
    public String ProductDetail(Model model , @PathVariable("id") Integer id) {
        Product item = productSV.findById(id);
        List<Product> product2 = this.productSV.finTop6Product();
        model.addAttribute("item", item);
        model.addAttribute("product2", product2);
        return "layout/ProductDetail";
    }

    @GetMapping("/user/login")
    public String login(Model model) {

        return "user/login";
    }

    @GetMapping("user/contact")
    public String contact(Model model) {
        return "user/contact";
    }

    @GetMapping("user/register")
    public String register(Model model) {
        return "user/register";
    }

    @GetMapping("user/info")
    public String info(Model model) {
        return "user/info";
    }
}

