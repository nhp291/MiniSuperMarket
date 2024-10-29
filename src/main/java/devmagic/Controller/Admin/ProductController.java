package devmagic.Controller.Admin;

import devmagic.Model.Product;
import devmagic.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/Products")
public class ProductController {

    @Autowired
    private ProductService productService; // Sử dụng ProductServiceImpl

    @GetMapping("/ProductList")
    public String productList(Model model) {
        List<Product> products = productService.getAllProducts();
        if (products == null) {
            products = new ArrayList<>(); // Đảm bảo danh sách không NULL
        }
        model.addAttribute("products", products);
        model.addAttribute("pageTitle", "Danh sách sản phẩm");
        model.addAttribute("viewName", "admin/menu/ProductList");
        return "admin/layout";
    }


    // Hiển thị form thêm sản phẩm
    @GetMapping("/AddProduct")
    public String addProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("pageTitle", "Thêm sản phẩm");
        model.addAttribute("viewName", "admin/menu/AddProduct");
        return "admin/layout";
    }

    // Xử lý việc tạo sản phẩm mới
    @PostMapping("/AddProduct")
    public String addProduct(@ModelAttribute Product product) {
        productService.createProduct(product);
        return "redirect:/Products/ProductList";
    }

    // Hiển thị form chỉnh sửa sản phẩm
    @GetMapping("/EditProduct/{id}")
    public String editProductForm(@PathVariable Integer id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("pageTitle", "Chỉnh sửa sản phẩm");
        model.addAttribute("viewName", "admin/menu/EditProduct");
        return "admin/layout";
    }

    // Xử lý việc cập nhật sản phẩm
    @PostMapping("/UpdateProduct/{id}")
    public String updateProduct(@PathVariable Integer id, @ModelAttribute Product product) {
        productService.updateProduct(id, product);
        return "redirect:/Products/ProductList";
    }

    // Xóa sản phẩm theo ID
    @GetMapping("/DeleteProduct/{id}")
    public String deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return "redirect:/Products/ProductList";
    }
}
