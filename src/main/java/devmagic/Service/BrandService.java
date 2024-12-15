package devmagic.Service;

import devmagic.Model.Brand;
import devmagic.Model.Product;
import devmagic.Reponsitory.BrandRepository;
import devmagic.Reponsitory.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BrandService {
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private ProductRepository productRepository;

    // Lấy danh sách tất cả thương hiệu
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    // Lấy thương hiệu theo ID
    public Optional<Brand> getBrandById(Integer id) {
        return brandRepository.findById(id);
    }

    // Tạo mới thương hiệu
    public Brand createBrand(Brand brand) {
        return brandRepository.save(brand);
    }

    // Cập nhật thương hiệu
    public Brand updateBrand(Integer id, Brand brand) {
        return brandRepository.findById(id)
                .map(existingBrand -> {
                    existingBrand.setBrandName(brand.getBrandName());
                    existingBrand.setDescription(brand.getDescription());
                    existingBrand.setImageUrl(brand.getImageUrl());
                    return brandRepository.save(existingBrand);
                })
                .orElseThrow(() -> new IllegalArgumentException("Brand with ID " + id + " not found"));
    }

    // Xóa thương hiệu
    @Transactional
    public void deleteBrand(Integer id) {
        Optional<Brand> brandOptional = brandRepository.findById(id);
        if (brandOptional.isPresent()) {
            Brand brand = brandOptional.get();

            // Xóa liên kết giữa sản phẩm và thương hiệu
            List<Product> products = productRepository.findByBrand(brand);
            for (Product product : products) {
                product.setBrand(null);
            }
            productRepository.saveAll(products);

            // Xóa thương hiệu
            brandRepository.delete(brand);
        } else {
            throw new IllegalArgumentException("Thương hiệu với ID " + id + " không tồn tại.");
        }
    }


    // Đếm tổng số thương hiệu
    public long getTotalBrands() {
        return brandRepository.count();
    }
}
