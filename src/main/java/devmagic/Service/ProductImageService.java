package devmagic.Service;

import devmagic.Model.ProductImage;
import devmagic.Reponsitory.ProductImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductImageService {
    @Autowired
    private ProductImageRepository productImageRepository;

    public List<ProductImage> getAllImages() {
        return productImageRepository.findAll();
    }

    public ProductImage createImage(ProductImage image) {
        return productImageRepository.save(image);
    }

    public ProductImage updateImage(Integer id, ProductImage image) {
        image.setImageId(id);
        return productImageRepository.save(image);
    }

    public void deleteImage(Integer id) {
        productImageRepository.deleteById(id);
    }
}

