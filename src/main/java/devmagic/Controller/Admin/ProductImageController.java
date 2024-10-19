package devmagic.Controller.Admin;

import devmagic.Model.ProductImage;
import devmagic.Service.ProductImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-images")
public class ProductImageController {
    @Autowired
    private ProductImageService productImageService;

    @GetMapping
    public List<ProductImage> getAllImages() {
        return productImageService.getAllImages();
    }

    @PostMapping
    public ProductImage createImage(@RequestBody ProductImage image) {
        return productImageService.createImage(image);
    }

    @PutMapping("/{id}")
    public ProductImage updateImage(@PathVariable Integer id, @RequestBody ProductImage image) {
        return productImageService.updateImage(id, image);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable Integer id) {
        productImageService.deleteImage(id);
        return ResponseEntity.noContent().build();
    }
}

