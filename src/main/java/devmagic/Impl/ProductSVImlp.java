package devmagic.Impl;

import devmagic.Model.Product;
import devmagic.Reponsitory.ProductRepository;
import devmagic.Service.ProductSV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductSVImlp implements ProductSV {
    @Autowired
    ProductRepository productRepository;

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Product findById(Integer id) {
        return productRepository.findById(id).get();
    }

    @Override
    public Page<Product> getall(Integer pageable) {
         Pageable page = PageRequest.of(pageable-1, 8);
        return this.productRepository.findAll(page);
    }

    @Override
    public List<Product> searchProduct(String keyword) {

        return this.productRepository.searchProduct(keyword);
    }


    @Override
    public Page<Product> seachProduct(String keyword, Integer pageNo) {
        List list = this.searchProduct(keyword);
        Pageable pageable = PageRequest.of(pageNo, 8);
         Integer start = (int) pageable.getOffset();
         Integer end = (int) ((pageable.getOffset() + pageable.getPageSize()) > list.size() ? list.size() : pageable.getOffset() + pageable.getPageSize());
         list = list.subList(start, end);
        return new PageImpl<Product>(list, pageable, this.searchProduct(keyword).size());
    }

    @Override
    public Page<Product> getForAll(Integer pageable) {
        Pageable page = PageRequest.of(pageable +4, 6);
        return this.productRepository.findAll(page);
    }
}
