package devmagic.Controller;

import devmagic.Model.Product;
import devmagic.Model.ProductImage;
import devmagic.Repository.ProductRepository;
import devmagic.Service.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FirebaseService firebaseService;

    @GetMapping("/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "addProduct";
    }

    @PostMapping("/add")
    public String addProduct(
            @ModelAttribute("product") Product product,
            @RequestParam("images") MultipartFile[] images
    ) {
        List<ProductImage> productImages = new ArrayList<>();

        // Upload từng hình ảnh lên Firebase
        for (MultipartFile image : images) {
            String imageUrl = firebaseService.uploadFile(image); // Gọi service để upload hình ảnh
            ProductImage productImage = new ProductImage();
            productImage.setImageUrl(imageUrl);
            productImage.setProduct(product);
            productImages.add(productImage);
        }

        product.setImages(productImages);
        productRepository.save(product); // Lưu sản phẩm và hình ảnh vào DB

        return "redirect:/product/list"; // Redirect về danh sách sản phẩm sau khi thêm
    }
}
