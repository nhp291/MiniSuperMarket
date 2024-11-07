package devmagic.Controller.Admin;

import devmagic.Model.Product;
import devmagic.Service.ProductService;
import devmagic.Service.CategoryService;
import devmagic.Service.BrandService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/Products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @GetMapping("/ProductList")
    public String productList(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        model.addAttribute("pageTitle", "Danh sách sản phẩm");
        model.addAttribute("viewName", "admin/menu/ProductList");
        return "admin/layout";
    }

    @GetMapping("/AddProduct")
    public String addProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("pageTitle", "Thêm sản phẩm");
        model.addAttribute("viewName", "admin/menu/AddProduct");
        return "admin/layout";
    }

    @PostMapping("/AddProduct")
    public String addProduct(@Valid @ModelAttribute Product product, BindingResult bindingResult,
                             @RequestParam("productImage") MultipartFile file, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("brands", brandService.getAllBrands());
            model.addAttribute("pageTitle", "Thêm sản phẩm");
            model.addAttribute("viewName", "admin/menu/AddProduct");
            return "admin/layout";
        }

        productService.createProduct(product, file);
        return "redirect:/Products/ProductList";
    }

    @GetMapping("/EditProduct/{id}")
    public String editProductForm(@PathVariable Integer id, Model model) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return "redirect:/Products/ProductList";
        }
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("pageTitle", "Chỉnh sửa sản phẩm");
        model.addAttribute("viewName", "admin/menu/AddProduct");
        return "admin/layout";
    }

    @PostMapping("/UpdateProduct/{id}")
    public String updateProduct(@PathVariable Integer id, @Valid @ModelAttribute Product product,
                                BindingResult bindingResult, @RequestParam("productImage") MultipartFile file, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("brands", brandService.getAllBrands());
            model.addAttribute("pageTitle", "Chỉnh sửa sản phẩm");
            model.addAttribute("viewName", "admin/menu/EditProduct");
            return "admin/layout";
        }

        productService.updateProduct(id, product, file);
        return "redirect:/Products/ProductList";
    }

    @GetMapping("/DeleteProduct/{id}")
    public String deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return "redirect:/Products/ProductList";
    }
}