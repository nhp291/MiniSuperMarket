package devmagic.Service;

import devmagic.Model.Brand;
import devmagic.Reponsitory.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandService {
    @Autowired
    private BrandRepository brandsRepository;

    public List<Brand> getAllBrands() {
        return brandsRepository.findAll();
    }

    public Brand createBrand(Brand brand) {
        return brandsRepository.save(brand);
    }

    public Brand updateBrand(Integer id, Brand brand) {
        brand.setBrandId(id);
        return brandsRepository.save(brand);
    }

    public void deleteBrand(Integer id) {
        brandsRepository.deleteById(id);
    }
}
