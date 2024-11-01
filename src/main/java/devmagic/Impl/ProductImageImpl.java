package devmagic.Impl;

import devmagic.Model.ProductImage;
import devmagic.Reponsitory.ProductImageRepository;
import devmagic.Service.ProductImageSV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductImageImpl implements ProductImageSV {
    @Autowired
    ProductImageRepository productImageRepository;

    @Override
    public List<ProductImage> findAll() {
        return productImageRepository.findAll();
    }
}
