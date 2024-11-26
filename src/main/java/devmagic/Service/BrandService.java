package devmagic.Service;

import devmagic.Model.Brand;
import devmagic.Reponsitory.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BrandService {
    @Autowired
    private BrandRepository brandRepository;

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
    public void deleteBrand(Integer id) {
//        if (brandRepository.existsById(id)) {
//            brandRepository.deleteById(id);
//        } else {
//            throw new IllegalArgumentException("Brand with ID " + id + " not found");
//        }
        brandRepository.deleteById(id);
    }

    // Đếm tổng số thương hiệu
    public long getTotalBrands() {
        return brandRepository.count();
    }
}
