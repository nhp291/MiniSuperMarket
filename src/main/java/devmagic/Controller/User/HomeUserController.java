package devmagic.Controller.User;



import devmagic.Model.Product;
import devmagic.Service.ProductSV;
import devmagic.Service.ProductService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.Optional;

@Controller
public class HomeUserController {
    @Autowired
    ProductService productService;

    @RequestMapping("/layout/Home")
    public String Home(
            Model model,
            @Param("keyword") String keyword,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "minPrice", required = false) Double minPrice,
            @RequestParam(name = "maxPrice", required = false) Double maxPrice,
            HttpServletRequest request) {

        // Lấy dữ liệu từ cookie (ví dụ lấy username từ cookie)
        String username = getUsernameFromCookies(request);
        Integer accountId = getAccountIdFromCookies(request);

        if (username != null) {
            model.addAttribute("username", username); // Thêm vào model để hiển thị trên trang
        }
        if (accountId != null) {
            model.addAttribute("accountId", accountId); // Thêm vào model để hiển thị trên trang
        }

        Page<Product> list;
        List<Product> product2 = this.productService.finTop6Product();
        Page<Product> list1 = this.productService.getForAll(pageNo);

        // Kiểm tra keyword (tìm kiếm) hoặc bộ lọc giá
        if (keyword != null) {
            list = this.productService.seachProduct(keyword, pageNo);
            model.addAttribute("keyword", keyword);
        } else if (minPrice != null || maxPrice != null) {
            list = this.productService.filterProductsByPrice(minPrice, maxPrice, pageNo);
            model.addAttribute("minPrice", minPrice);
            model.addAttribute("maxPrice", maxPrice);
        } else {
            list = this.productService.getall(pageNo); // Lấy toàn bộ sản phẩm nếu không có bộ lọc
        }

        model.addAttribute("totalPage", list.getTotalPages());
        model.addAttribute("currenPage", pageNo);
        model.addAttribute("product", list);
        model.addAttribute("product1", list1);
        model.addAttribute("product2", product2);

        return "layout/Home";
    }


    // Phương thức để lấy username từ cookie
    private String getUsernameFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("username".equals(cookie.getName())) {  // Kiểm tra cookie tên 'username'
                    return cookie.getValue();  // Trả về giá trị của cookie 'username'
                }
            }
        }
        return null; // Nếu không có cookie 'username'
    }

    // Phương thức để lấy accountId từ cookie
    private Integer getAccountIdFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accountId".equals(cookie.getName())) {  // Kiểm tra cookie tên 'accountId'
                    try {
                        return Integer.parseInt(cookie.getValue());  // Chuyển giá trị cookie thành Integer
                    } catch (NumberFormatException e) {
                        // Nếu không thể chuyển đổi giá trị của cookie thành Integer, log lỗi
                        System.out.println("Lỗi khi chuyển đổi giá trị accountId từ cookie: " + e.getMessage());
                        return null;
                    }
                }
            }
        }
        return null; // Nếu không có cookie 'accountId'
    }


    @RequestMapping("/layout/Product")
    public String Product(Model model,@RequestParam("cid")Optional<String> cid) {
        if (cid.isPresent()) {
            List<Product> list = this.productService.findByCategoryId(cid.get());
            model.addAttribute("product", list);
        }else{
            List<Product> list = this.productService.findAll();
            model.addAttribute("product", list);
        }
        return "layout/Product";
    }


    @RequestMapping("/product/detail/{id}")
    public String ProductDetail(Model model , @PathVariable("id") Integer id) {
        Product item = productService.findById(id);
        List<Product> product2 = this.productService.finTop6Product();
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

//    @GetMapping("user/info")
//    public String info(Model model) {
//        return "user/info";
//    }
}

