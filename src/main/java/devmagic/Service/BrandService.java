package devmagic.Service;

import devmagic.Model.Brand;
import devmagic.Reponsitory.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandService {
    @Autowired
    private BrandRepository brandRepository;

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    public Brand getBrandById(Integer id) {
        return brandRepository.findById(id).orElse(null); // Handle null case properly in production
    }

    public Brand createBrand(Brand brand) {
        return brandRepository.save(brand);
    }

    public Brand updateBrand(Integer id, Brand brand) {
        // Check if the brand exists and update
        Brand existingBrand = brandRepository.findById(id).orElse(null);
        if (existingBrand != null) {
            existingBrand.setBrandName(brand.getBrandName());
            existingBrand.setDescription(brand.getDescription());
            existingBrand.setImageUrl(brand.getImageUrl());
            return brandRepository.save(existingBrand);
        }
        return null; // In production, handle the case properly
    }


    public void deleteBrand(Integer id) {
        brandRepository.deleteById(id);
    }

    public long getTotalBrands() {
        return brandRepository.count();  // Sử dụng brandsRepository thay vì brandRepository
    }
}
