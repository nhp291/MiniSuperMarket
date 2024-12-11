    package devmagic.Controller.Admin;

    import devmagic.Dto.ProductDTO;
    import devmagic.Model.*;
    import devmagic.Reponsitory.*;
    import devmagic.Service.BrandService;
    import devmagic.Service.CategoryService;
    import devmagic.Service.ProductService;
    import jakarta.transaction.Transactional;
    import jakarta.validation.Valid;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.data.domain.*;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.multipart.MultipartFile;

    import java.io.File;
    import java.io.IOException;
    import java.math.BigDecimal;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.util.List;
    import java.util.Optional;

    @Controller
    @RequestMapping("/Products")
    public class ProductController {

        @Autowired
        private ProductRepository productRepository;

        @Autowired
        private CategoryRepository categoryRepository;

        @Autowired
        private WarehouseRepository warehouseRepository;

        @Autowired
        private BrandRepository brandRepository;

        @Autowired
        private ProductImageRepository productImageRepository;

        @Autowired
        private ProductService productService;

        @Autowired
        private BrandService brandService;

        @Autowired
        private CategoryService categoryService;

        @GetMapping("/ProductList")
        public String viewProductList(Model model,
                                      @RequestParam(value = "page", defaultValue = "0") int page,
                                      @RequestParam(value = "size", defaultValue = "10") int size,
                                      @RequestParam(value = "search", required = false) String search,
                                      @RequestParam(value = "cat", required = false) Integer cat,
                                      @RequestParam(value = "brand", required = false) Integer brand,
                                      @RequestParam(value = "filter", required = false) String filter) {

            Pageable pageable = Pageable.ofSize(size).withPage(page);
            Page<Product> productPage;
            System.out.println("Category ID: " + cat);
            System.out.println("Brand ID: " + brand);

            // Kiểm tra các tùy chọn filter
            if ("outOfStock".equals(filter)) {
                productPage = new PageImpl<>(productService.getOutOfStockProducts(pageable));
            } else if ("nearlyOutOfStock".equals(filter)) {
                productPage = new PageImpl<>(productService.getNearlyOutOfStockProducts(pageable));
            } else {
                // Xử lý các trường hợp tìm kiếm, lọc theo danh mục, thương hiệu
                if (search != null && !search.isEmpty()) {
                    if (cat != null && brand != null) {
                        productPage = productService.findBySearchCatBrand(search, cat, brand, pageable);
                    } else if (cat != null) {
                        productPage = productService.findBySearchCat(search, cat, pageable);
                    } else if (brand != null) {
                        productPage = productService.findBySearchBrand(search, brand, pageable);
                    } else {
                        productPage = productService.findBySearch(search, pageable);
                    }
                } else if (cat != null && brand != null) {
                    productPage = productService.findByCatBrand(cat, brand, pageable);
                } else if (cat != null) {
                    productPage = productService.findByCat(cat, pageable);
                } else if (brand != null) {
                    productPage = productService.findByBrand(brand, pageable);
                } else {
                    productPage = productService.getAll(pageable);
                }
            }

            model.addAttribute("products", productPage.getContent());
            model.addAttribute("pageTitle", "Product List");
            model.addAttribute("viewName", "admin/menu/ProductList");
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", productPage.getTotalPages());
            model.addAttribute("totalItems", productPage.getTotalElements());
            model.addAttribute("search", search);
            model.addAttribute("cat", cat);
            model.addAttribute("brand", brand);
            model.addAttribute("filter", filter); // Truyền filter vào Model để giữ trạng thái khi thay đổi trang
            model.addAttribute("categories", categoryService.getAllCategories()); // add categories to model
            model.addAttribute("brands", brandService.getAllBrands()); // add brands to model

            return "admin/layout";
        }

        @GetMapping("/AddProduct")
        public String createProductForm(Model model) {
            model.addAttribute("product", new Product());
            model.addAttribute("productDTO", new ProductDTO());
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("warehouses", warehouseRepository.findAll());
            model.addAttribute("brands", brandRepository.findAll());
            model.addAttribute("pageTitle", "Add Product");
            model.addAttribute("viewName", "admin/menu/AddProduct");
            return "admin/layout";
        }

        @PostMapping("/AddProduct")
        public String createProduct(@ModelAttribute @Valid ProductDTO productDTO,
                                    @RequestParam(value = "imageFiles", required = false) List<MultipartFile> files,
                                    Model model) {
            if (files == null || files.isEmpty() || files.stream().allMatch(MultipartFile::isEmpty)) {
                model.addAttribute("error", "Vui lòng tải lên ít nhất một hình ảnh.");
                model.addAttribute("productDTO", productDTO);
                model.addAttribute("categories", categoryRepository.findAll());
                model.addAttribute("warehouses", warehouseRepository.findAll());
                model.addAttribute("brands", brandRepository.findAll());
                model.addAttribute("pageTitle", "Add Product");
                model.addAttribute("viewName", "admin/menu/AddProduct");
                return "admin/layout";
            } else {
                for (MultipartFile file : files) {
                    System.out.println("Đã nhận file: " + file.getOriginalFilename());
                }
            }

            try {
                // Lấy đối tượng category, warehouse, và brand từ DTO
                Category category = categoryRepository.findById(productDTO.getCategoryId())
                        .orElseThrow(() -> new IllegalArgumentException("Danh mục không tồn tại"));
                Warehouse warehouse = warehouseRepository.findById(productDTO.getWarehouseId())
                        .orElseThrow(() -> new IllegalArgumentException("Kho hàng không tồn tại"));
                Brand brand = brandRepository.findById(productDTO.getBrandId())
                        .orElseThrow(() -> new IllegalArgumentException("Thương hiệu không tồn tại"));

                // Tạo sản phẩm từ DTO
                Product product = productService.convertToProduct(productDTO, category, warehouse, brand);
                Product savedProduct = productRepository.save(product);  // Lưu sản phẩm vào DB

                // Xử lý lưu ảnh
                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        String fileName = productService.saveImage(file); // Gọi saveImage từ ProductService
                        ProductImage productImage = new ProductImage();
                        productImage.setProduct(savedProduct);
                        productImage.setImageUrl(fileName); // Lưu tên file vào database
                        productImageRepository.save(productImage);
                    }
                }

                return "redirect:/Products/ProductList";
            } catch (IllegalArgumentException e) {
                model.addAttribute("error", "Lỗi: " + e.getMessage());
            } catch (IOException e) {
                model.addAttribute("error", "Đã xảy ra lỗi trong quá trình lưu ảnh: " + e.getMessage());
            } catch (Exception e) {
                model.addAttribute("error", "Đã xảy ra lỗi không xác định: " + e.getMessage());
            }

            model.addAttribute("productDTO", productDTO);
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("warehouses", warehouseRepository.findAll());
            model.addAttribute("brands", brandRepository.findAll());
            model.addAttribute("pageTitle", "Add Product");
            model.addAttribute("viewName", "admin/menu/AddProduct");
            return "admin/layout";
        }

        @GetMapping("/EditProduct/{id}")
        public String editProductForm(@PathVariable("id") int id, Model model) {
            Optional<Product> productOpt = productRepository.findById(id);
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                ProductDTO productDTO = new ProductDTO();
                productDTO.setName(product.getName());
                productDTO.setDescription(product.getDescription());
                productDTO.setPrice(product.getPrice().doubleValue());
                productDTO.setSale(product.getSale() != null ? product.getSale().doubleValue() : 0);
                productDTO.setStockQuantity(product.getStockQuantity());
                productDTO.setCategoryId(product.getCategory().getCategoryId());
                productDTO.setWarehouseId(product.getWarehouse().getWarehouseId());
                productDTO.setBrandId(product.getBrand().getBrandId());

                // Thêm các trường unit và origin
                productDTO.setUnit(product.getUnit());
                productDTO.setOrigin(product.getOrigin());

                model.addAttribute("productDTO", productDTO);
                model.addAttribute("images", product.getImages());
                model.addAttribute("categories", categoryRepository.findAll());
                model.addAttribute("warehouses", warehouseRepository.findAll());
                model.addAttribute("brands", brandRepository.findAll());
                model.addAttribute("pageTitle", "Edit Product");
                model.addAttribute("viewName", "admin/menu/AddProduct");
                return "admin/layout";
            }
            model.addAttribute("error", "Sản phẩm không tồn tại");
            return "redirect:/Products/ProductList";
        }


        @PostMapping("/EditProduct/{id}")
        public String updateProduct(@PathVariable("id") int id, @ModelAttribute @Valid ProductDTO productDTO,
                                    @RequestParam(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
                                    Model model) throws IOException {

            // Kiểm tra tính hợp lệ của productDTO
            if (productDTO.getBrandId() == null || !brandRepository.existsById(productDTO.getBrandId())) {
                model.addAttribute("error", "Thương hiệu không hợp lệ");
                model.addAttribute("categories", categoryRepository.findAll());
                model.addAttribute("warehouses", warehouseRepository.findAll());
                model.addAttribute("brands", brandRepository.findAll());
                return "admin/menu/AddProduct";
            }

            Optional<Product> existingProductOpt = productRepository.findById(id);
            if (existingProductOpt.isPresent()) {
                Product existingProduct = existingProductOpt.get();

                // Cập nhật thông tin sản phẩm
                existingProduct.setName(productDTO.getName());
                existingProduct.setCategory(categoryRepository.findById(productDTO.getCategoryId()).orElse(null));
                existingProduct.setPrice(new BigDecimal(productDTO.getPrice()));
                existingProduct.setSale(new BigDecimal(productDTO.getSale()));
                existingProduct.setStockQuantity(productDTO.getStockQuantity());
                existingProduct.setDescription(productDTO.getDescription());
                existingProduct.setWarehouse(warehouseRepository.findById(productDTO.getWarehouseId()).orElse(null));
                existingProduct.setBrand(brandRepository.findById(productDTO.getBrandId()).orElse(null));

                // Cập nhật Unit và Origin
                existingProduct.setUnit(productDTO.getUnit());
                existingProduct.setOrigin(productDTO.getOrigin());

                // Lưu sản phẩm đã cập nhật
                productRepository.save(existingProduct);

                // Nếu có hình ảnh mới được tải lên
                if (imageFiles != null && imageFiles.stream().anyMatch(file -> !file.isEmpty())) {
                    // Xóa các hình ảnh cũ chỉ khi có hình ảnh mới
                    List<ProductImage> existingImages = productImageRepository.findByProduct_ProductId(id);
                    productImageRepository.deleteAll(existingImages);

                    // Lưu hình ảnh mới
                    for (MultipartFile file : imageFiles) {
                        if (!file.isEmpty()) {
                            String savedFileName = productService.saveImage(file);
                            ProductImage newImage = new ProductImage();
                            newImage.setImageUrl(savedFileName);
                            newImage.setProduct(existingProduct);
                            productImageRepository.save(newImage);
                        }
                    }
                }

                return "redirect:/Products/ProductList";
            }

            model.addAttribute("error", "Sản phẩm không tồn tại");
            return "redirect:/Products/ProductList";
        }

        @Transactional
        @GetMapping("/DeleteProduct/{id}")
        public String deleteProduct(@PathVariable("id") int id) {
            Optional<Product> productOpt = productRepository.findById(id);
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                productImageRepository.deleteAllByProduct(product);
                productRepository.delete(product);
            }
            return "redirect:/Products/ProductList";
        }
    }