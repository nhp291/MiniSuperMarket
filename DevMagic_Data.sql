CREATE DATABASE DevMagic;

-- Dữ liệu

INSERT INTO Role (role_name) VALUES 
(N'Admin'), -- Vai trò Admin
(N'User'); -- Vai trò User


INSERT INTO Auth_Providers (provider_name) VALUES 
(N'Zalo'), -- Nhà cung cấp Zalo
(N'Google'), -- Nhà cung cấp Google
(N'Facebook'), -- Nhà cung cấp Facebook
(N'Form'); -- Nhà cung cấp Đăng ký thông thường


INSERT INTO Account (username, password, email, phone_number, address, image_url, role_id) VALUES 
(N'HoaiPhong', 'HoaiPhong@123', 'nhp2901@gmail.com', '0976260335', N'Gò Vấp, HCM', NULL, 1),
(N'Phong', 'HoaiPhong@123', 'phongnhps31815@fpt.edu.vn', '0976260335', N'Gò Vấp, HCM', NULL, 2),
(N'TuanHuy', 'TuanHuy@123', 'huyphan0843028525@gmail.com', '0843028525', N'Tân Bình, HCM', NULL, 1),
(N'Huy', 'TuanHuy@123', 'Huyphan151004@gmail.com', '0843028525', N'Tân Bình, HCM', NULL, 2),
(N'HongPhuc', 'HongPhuc@123', 'Hongphucmango@gmail.com', '0363824426', N'Tân Phú, HCM', NULL, 1),
(N'Phuc', 'HongPhuc@123', 'Phucnhps31732@fpt.edu.vn', '0363824426', N'Tân Phú, HCM', NULL, 2),
(N'ThanhPhat', 'ThanhPhat@123', 'Phathtps31440@fpt.edu.vn', '0915765058', N'Tân Bình, HCM', NULL, 1),
(N'Phat', 'ThanhPhat@123', 'Phatanhvu147963kk@gmail.com', '0915765058', N'Tân Bình, HCM', NULL, 2);



INSERT INTO Account_Auth_Provider (account_id, provider_id, provider_user_id) VALUES 
(1, 4, 1),
(2, 4, 1),
(3, 4, 1),
(4, 4, 1),
(5, 4, 1),
(6, 4, 1),
(7, 4, 1),
(8, 4, 1);



INSERT INTO Categories (category_name, image_url, description) VALUES 
(N'Thế giới trái cây', 'cateTraiCay', N'Mô tả cho danh mục Thế giới trái cây'),
(N'Rau củ quả', 'cateRauCu', N'Mô tả cho danh mục Rau củ quả'),
(N'Thịt, cá, trứng, hải sản', 'cateThit', N'Mô tả cho danh mục Thịt, cá, trứng, hải sản'),
(N'Sữa, bơ, phô mai', 'cateSua', N'Mô tả cho danh mục Sữa, bơ, phô mai'),
(N'Thực phẩm đông-mát', 'cateDongMat', N'Mô tả cho danh mục Thực phẩm đông-mát'),
(N'Bánh kẹo, ngũ cốc ăn sáng', 'cateBanhKeo', N'Mô tả cho danh mục Bánh kẹo, ngũ cốc ăn sáng'),
(N'Dầu ăn, nước chấm, gia vị', 'cateDauAn', N'Mô tả cho danh mục Dầu ăn, nước chấm, gia vị'),
(N'Gạo, đậu, bột, đồ khô', 'cateGaoBot', N'Mô tả cho danh mục Gạo, đậu, bột, đồ khô'),
(N'Đồ hộp, xúc xích, lạp xưởng', 'cateDoHop', N'Mô tả cho danh mục Đồ hộp, xúc xích, lạp xưởng'),
(N'Mì, miến, bún, cháo,...', 'cateMi', N'Mô tả cho danh mục Mì, miến, bún, cháo,...'),
(N'Tráng miệng, ăn xế', 'cateTrangMieng', N'Mô tả cho danh mục Tráng miệng, ăn xế'),
(N'Bia, rượu, trà, nước,...', 'cateBia', N'Mô tả cho danh mục Bia, rượu, trà, nước,...'),
(N'Bữa ăn tiện lợi', 'cateBuaAnTienLoi', N'Mô tả cho danh mục Bữa ăn tiện lợi'),
(N'Đồ dùng gia đình', 'cateDoDungGiaDinh', N'Mô tả cho danh mục Đồ dùng gia đình'),
(N'Chăm sóc cá nhân', 'cateCSCN', N'Mô tả cho danh mục Chăm sóc cá nhân'),
(N'Vệ sinh nhà cửa', 'cateVSNC', N'Mô tả cho danh mục Vệ sinh nhà cửa'),
(N'Thực phẩm chăm sóc sức khỏe', 'cateThucPhapCSSK', N'Mô tả cho danh mục Thực phẩm chăm sóc sức khỏe');




INSERT INTO Brands (brand_name, image_url, description) VALUES 
(N'Khuyến mãi hot', 'hotdeal', 'Mô tả cho thương hiệu Khuyến mãi hot'),
(N'Mua chung giá tốt', 'GiaTot', 'Mô tả cho thương hiệu Mua chung giá tốt'),
(N'Thịt tươi sạch', 'ThitTuoiSach', 'Mô tả cho thương hiệu Thịt tươi sạch'),
(N'Tráng miệng ngọt mát', 'TrangMieng', 'Mô tả cho thương hiệu Tráng miệng ngọt mát'),
(N'Giá sốc cuối tuần', 'GiaSoc', 'Mô tả cho thương hiệu Giá sốc cuối tuần'),
(N'Bánh tươi mới nướng', 'BanhTuoi', 'Mô tả cho thương hiệu Bánh tươi mới nướng');



INSERT INTO Warehouse (warehouse_name, location) VALUES 
(N'Kho 1', N'Địa điểm 1'), -- Kho 1
(N'Kho 2', N'Địa điểm 2'); -- Kho 2




INSERT INTO Product (name, description, price, sale, stock_quantity, category_id, brand_id, warehouse_id) VALUES 
(N'Túi Táo Envy', N'Túi Táo Envy size 90-100 (1 Kg)', 149.000, NULL, 50, 1, 1, 1),
(N'Dừa xiêm xanh', N'Combo dừa xiêm xanh 5 quả size 300-400g (1 Túi)', 69.000, NULL, 50, 1, 1, 1),
(N'Nho xanh Mỹ', N'Nho xanh Mỹ hàng air túi 1kg (1 Túi)', 100, NULL, 50, 1, 1, 1),
(N'Táo Mỹ & Nam Phi', N'Táo Mỹ & Nam Phi túi 1.5kg (1 Túi)', 98.000, 89.000, 50, 1, 1, 1),
(N'Chuối già Nam Mỹ', N'Chuối già Nam Mỹ kid nải 550g (1 Nải)', 18.950, NULL, 50, 1, 1, 1),
(N'Kiwi New Zealand', N'Kiwi New Zealand Zespri vàng (1kg)', 239.000, 195.000, 50, 1, 1, 1),
(N'Nho xanh Mỹ không hạt', N'Nho xanh Mỹ không hạt hộp 450g (1 Hộp)', 179.000, 139.000, 50, 1, 1, 2),
(N'Mít giống Thái', N'Mít giống Thái cắt miếng 700GR (1 Vỉ)', 42.000, NULL, 50, 1, 1, 1),
(N'Táo New Zealand', N'Táo New Zealand Rockit 4 trái (1 Ống)', 139.000, 129.000, 50, 1, 1, 2),
(N'Me ngọt Thái Lan', N'Me ngọt Thái Lan hộp 450g (1 Hộp)', 79.000, 69.000, 50, 1, 1, 1),
(N'Táo Mỹ & Nam Phi mini', N'Táo Mỹ & Nam Phi mini hộp 1kg (1 Hộp)', 79.000, 69.000, 50, 1, 1, 1),
(N'Chuối Haha', N'Chuối Haha 5 trái (1 Chùm)', 26.900, NULL, 50, 1, 1, 1),
(N'Dừa xiêm', N'Combo dừa xiêm túi 3 trái (1 Túi)', 49.000, NULL, 50, 1, 1, 1),
(N'Mận An Phước', N'Mận An Phước vỉ từ 900g (1 Vỉ)', 53.100, NULL, 50, 1, 1, 2),
(N'Lê nâu nội địa Trung', N'Lê nâu nội địa Trung quả 550G (1 quả)', 32.450, 20.500, 50, 1, 1, 1),
(N'Cam Úc Navel', N'Cam Úc Navel quả 380G (1 Quả)', 32.300, 30.000, 50, 1, 1, 2),
(N'Quýt đường', N'Quýt đường lão túi 1kg (1 Túi)', 65.000, NULL, 50, 1, 1, 1),
(N'Táo New Zealand xanh', N'Táo New Zealand xanh (1kg)', 99.000, NULL, 50, 1, 1, 1),
(N'Hộp kiwi xanh', N'Hộp kiwi xanh Zespri 4 quả (1 Hộp)', 99.000, 89.000, 50, 1, 1, 2),
(N'Xoài cát chu vàng', N'Xoài cát chu vàng trái từ 300g (1 Trái)', 20.700, 16.800, 50, 1, 1, 1),
(N'Thanh long ruột trắng', N'Thanh long ruột trắng trái 450g (1 Trái)', 16.500, NULL, 50, 1, 1, 1),
(N'Chà là nguyên cành', N'Chà là nguyên cành Sanwan hộp 500g (1 Hộp)', 149.000, 128.000, 50, 1, 1, 2),
(N'Thanh long ruột tím hồng', N'Thanh long ruột tím hồng trái từ 400g (1 Trái)', 19.000, NULL, 50, 1, 1, 2),
(N'Dưa lưới vỏ vàng', N'Dưa lưới vỏ vàng ruột cam trái 2kg (1 Trái)', 118.000, 90.500, 50, 1, 1, 1),
(N'Chuối Bolaven', N'Chuối Bolaven nhánh 700G (1 Gói)', 46.000, NULL, 50, 1, 1, 1),
(N'Cam sành', N'Cam sành quả 150G (1 Quả)', 2.100, 1.300, 50, 1, 1, 1),
(N'Lê Nam Phi', N'Lê Nam Phi hộp 800g (1 Hộp)', 89.000, NULL, 50, 1, 1, 1),
(N'Bưởi da xanh', N'Bưởi da xanh trái 1.2kg (1 Trái)', 86.400, NULL, 50, 1, 1, 1),
(N'Dưa hấu ruột vàng', N'Dưa hấu ruột vàng trái từ 2.5kg (1 Trái)', 80.000, NULL, 50, 1, 1, 1),
(N'Nho mẫu đơn', N'Nho mẫu đơn Hàn Quốc 450G (1 Hộp)', 329.000, 275.000, 50, 1, 1, 1),
(N'Xoài Đài Loan', N'Xoài dài giống Đài Loan (1 kg)', 28.000, NULL, 50, 1, 1, 1),
(N'Dưa lưới ruột cam', N'Dưa lưới ruột cam trái từ 1.6kg (1 Trái)', 88.000, NULL, 50, 1, 1, 1),
(N'Ổi trân châu', N'Ổi trân châu ruột đỏ vỉ từ 700G (1 Vỉ)', 24.500, NULL, 50, 1, 1, 1),
(N'Bưởi ép nước', N'Bưởi ép nước quả từ 600g (1 Quả)', 12.500, 14.000, 50, 1, 1, 1),
(N'Vỉ chanh dây', N'Vỉ chanh dây (0.5 Kg)', 22.500, NULL, 50, 1, 1, 1);

INSERT INTO Product (name, description, price, sale, stock_quantity, category_id, brand_id, warehouse_id) VALUES 
(N'Xà lách thuỷ tinh', N'Xà lách thuỷ tinh thuỷ canh gói 200g (1 Gói)', 14.900, 12.500, 50, 2, 1, 1),
(N'Bông cải xanh VietGAP', N'Bông cải xanh VietGAP Đà Lạt bông 320G (1 Bông)', 27.200, NULL, 50, 2, 1, 1),
(N'Cà chua trứng', N'Cà chua trứng vỉ 400g (1 Vỉ)', 25.500, NULL, 50, 2, 1, 1),
(N'Dưa leo baby', N'Dưa leo baby Đà Lạt vỉ 500g (1 Vỉ)', 19.500, NULL, 50, 2, 1, 1),
(N'Rau nêm hỗn hợp', N'Rau nêm hỗn hợp VietGap gói 80g (1 Gói)', 11.500, NULL, 50, 2, 1, 1),
(N'Khoai tây Đà Lạt', N'Khoai tây Đà Lạt túi 500g (1 Túi)', 25.000, 19.000, 50, 2, 1, 1),
(N'Xà lách lô', N'Xà lách lô lô xanh thuỷ canh gói 200g (1 Gói)', 16.500, 14.900, 50, 2, 1, 1),
(N'Bắp cải trái tim', N'Bắp cải trái tim Đà Lạt VietGAP bắp 600g (1 Bắp)', 27.000, NULL, 50, 2, 1, 1),
(N'Bắp giống Mỹ', N'Bắp giống Mỹ cặp 500g (1 Cặp)', 18.900, NULL, 150, 2, 1, 1),
(N'Tỏi cô đơn', N'Tỏi cô đơn Trung Quốc gói 300g (1 Gói)', 52.000, NULL, 50, 2, 1, 1),
(N'Hành lá VietGAP', N'Hành lá VietGAP VietRAT 100G (1 Gói)', 10.000, NULL, 50, 2, 1, 1),
(N'Hành Lý Sơn', N'Hành Lý Sơn gói 300g (1 Gói)', 32.000, NULL, 50, 2, 1, 1),
(N'Hành tây F2', N'Hành tây F2 Đà Lạt size trung bi gói 500g (1 Gói)', 28.000, 20.900, 50, 2, 1, 2),
(N'Chanh không hạt', N'Chanh không hạt Global Gap vỉ 250g (1 Vỉ)', 11.000, NULL, 350, 2, 1, 1),
(N'Rau thơm hỗn hợp', N'Rau thơm hỗn hợp hữu cơ VietRat gói 150g (1 Gói)', 21.500, NULL, 150, 2, 1, 1),
(N'Khoai lang', N'Khoai lang giống Nhật túi 1kg (1 Túi)', 45.000, NULL, 270, 2, 1, 1),
(N'Canh chua', N'Canh chua Vườn Lài vỉ 600g (1 Vỉ)', 27.900, NULL, 20, 2, 1, 1),
(N'Khoai tây vàng', N'Khoai tây vàng giống Hà Lan DalatGAP túi 500g (1 Túi)', 40.500, NULL, 10, 2, 1, 1),
(N'Rau bồ ngót', N'Rau bồ ngót VietGap Trường Phát gói 300g (1 Gói)', 17.500, NULL, 70, 2, 1, 1),
(N'Cải thìa baby', N'Cải thìa baby DalatGap gói 300g (1 Gói)', 33.500, NULL, 50, 2, 1, 1),
(N'Ớt hiểm PTFARM', N'Ớt hiểm PTFARM gói 50g (1 Gói)', 8.000, NULL, 50, 2, 1, 1),
(N'Xà lách mỡ', N'Xà lách mỡ thuỷ canh gói 200g (1 Gói)', 14.900, 12.500, 35, 2, 1, 1),
(N'Cải thìa VietGAP', N'Cải thìa VietGAP gói 300g (1 Gói)', 14.500, NULL, 23, 2, 1, 1),
(N'Rau muống hữu cơ', N'Rau muống hữu cơ VietRat gói 300g (1 Gói)', 19.000, NULL, 75, 2, 1, 2),
(N'Rau muống baby', N'Rau muống baby hữu cơ VietRat gói 300g (1 Gói)', 21.500, NULL, 50, 2, 1, 1),
(N'Cải bó xôi', N'Cải bó xôi Đà Lạt gói 300g (1 Gói)', 33.500, NULL, 100, 2, 1, 1),
(N'Rau má lá nhỏ', N'Rau má lá nhỏ Trường Phát gói 200g (1 Gói)', 18.500, NULL, 50, 2, 1, 1),
(N'Ớt chuông đỏ', N'Ớt chuông đỏ VietGap PTFARM trái 180g (1 Trái)', 16.200, 10.600, 50, 2, 1, 1),
(N'Nấm đùi gà', N'Nấm đùi gà hữu cơ Bắc Âu gói 250g (1 Gói)', 33.000, 98.000, 230, 2, 1, 1),
(N'Hạt sen', N'Hạt sen tươi gói 200g (1 Gói)', 65.000, NULL, 370, 2, 1, 1),
(N'Dưa leo VietGAP', N'Dưa leo VietGAP Trường Phát vỉ 500g (1 Vỉ)', 19.000, 16.900, 50, 2, 1, 2),
(N'Ớt chuông baby', N'Ớt chuông baby màu PTFARM vỉ 350g (1 Vỉ)', 39.000, NULL, 50, 2, 1, 1),
(N'Cải thảo Đà Lạt', N'Cải thảo Đà Lạt bắp 1kg (1 Bắp)', 34.000, NULL, 260, 2, 1, 1),
(N'Nấm kim châm', N'Nấm kim châm hữu cơ Bắc Âu gói 150g (1 Gói)', 85.000, 72.900, 50, 2, 1, 1),
(N'Nấm đùi gà baby', N'Nấm đùi gà baby Việt Nam hữu cơ Bắc Âu gói 250g (1 Gói)', 39.000, NULL, 20, 2, 1, 1);


INSERT INTO Product (name, description, price, sale, stock_quantity, category_id, brand_id, warehouse_id) VALUES 
(N'Cá bống', N'Cá bống thệ HS 200g (1 gói)', 39.000, 28.000, 20, 3, 1, 1),
(N'Tôm đất', N'Tôm đất nguyên con HS 200g (1 gói)', 69.000, 53.000, 20, 3, 1, 1),
(N'Thịt heo', N'Thịt heo xay (1 Kg)', 129.000, 99.000, 25, 3, 1, 1),
(N'Cá hồi Faroe', N'Cá hồi Faroe phi lê (1KG)', 749.000, 599.000, 30, 3, 1, 1),
(N'Sườn non', N'Sườn non (1 Kg)', 259.000, NULL, 80, 3, 1, 2),
(N'Sườn già', N'Sườn già (1 Kg)', 169.000, NULL, 12, 3, 1, 1),
(N'Tôm thẻ lớn', N'Tôm thẻ lớn 30-35 con/kg (1 Kg)', 279.000, 239.000, 40, 3, 1, 1),
(N'Phi lê má', N'Phi lê má đùi không da Japfa', 119.000, 99.000, 10, 3, 1, 1),
(N'Phi lê ức gà', N'Phi lê ức gà không da (1 Kg)', 99.000, NULL, 15, 3, 1, 2),
(N'Nạc dăm', N'Nạc dăm (1 Kg)', 169.000, NULL, 45, 3, 1, 1),
(N'Ba rọi heo', N'Ba rọi heo (1 Kg)', 179.000, NULL, 20, 3, 1, 1),
(N'Chân giò heo', N'Chân giò heo trước (1 Kg)', 110.000, 99.000, 25, 3, 1, 1),
(N'Cánh giữa', N'Cánh giữa (1 Kg)', 159.000, NULL, 20, 3, 1, 1),
(N'Trứng gà tươi', N'Trứng gà tươi Premium size 55g+2g Gfood hộp 10 quả (1 Hộp)', 28.000, 26.500, 150, 3, 1, 1),
(N'Chân gà rút xương', N'Chân gà rút xương 3F (1 Kg)', 189.000, NULL, 20, 3, 1, 1),
(N'Cá lóc cắt lát', N'Cá lóc cắt lát Vietgap 300g (1 vỉ)', 65.000, NULL, 10, 3, 1, 1),
(N'Chân gà tươi', N'Chân gà tươi CP vỉ 500g', 39.900, NULL, 20, 3, 1, 1),
(N'Xương ức gà', N'Xương ức gà CP (1 Kg)', 31.000, NULL, 5, 3, 1, 2),
(N'Cá nục thuôn', N'Cá nục thuôn nguyên con làm sạch HS 500g (1 gói)', 55.000, 49.000, 32, 3, 1, 1),
(N'Mực nang', N'Mực nang làm sạch vỉ 300g (1 Vỉ)', 79.000, NULL, 20, 3, 1, 1),
(N'Cá diêu hồng', N'Cá diêu hồng phi lê VietGAP vỉ 300g (1 Vỉ)', 59.000, NULL, 13, 3, 1, 1),
(N'Đùi tỏi gà', N'Đùi tỏi gà Japfa (1 Kg)', 109.000, NULL, 35, 3, 1, 1),
(N'Trứng gà size XL', N'Trứng gà size XL Ba Huân hộp 10 quả (1 Hộp)', 36.000, 30.900, 20, 3, 1, 2),
(N'Ba chỉ bò', N'Ba chỉ bò ngũ cốc Úc đông lạnh Freshfoco gói 200g (1 Gói)', 75.000, NULL, 20, 3, 1, 2),
(N'Thăn ngoại bò Úc', N'Thăn ngoại bò Úc Hokubee 190 Kome88 gói 210g', 189.000, NULL, 20, 3, 1, 1),
(N'Hàu sữa', N'Hàu sữa Thái Bình Dương Hasubi gói 300g (1 Gói)', 83.000, NULL, 100, 3, 1, 1),
(N'Lườn cá hồi Nauy', N'Lườn cá hồi Nauy cao cấp Denti vỉ 300g (1 Vỉ)', 39.000, NULL, 20, 3, 1, 2),
(N'Cá saba Nauy', N'Cá saba Nauy Fillet Ocean Gift 250g (1 Gói)', 56.000, NULL, 40, 3, 1, 1),
(N'Cá hồi Faroe', N'Cá hồi Faroe phi lê (200g)', 119.000, 105.000, 34, 3, 1, 1),
(N'Râu bạch tuộc', N'Râu bạch tuộc chần đông lạnh 200g (1 vỉ)', 105.000, NULL, 30, 3, 1, 2),
(N'Gà ta giống', N'Gà ta giống Phan Rang nguyên con Vfoods (1Kg)', 185.000, NULL, 84, 3, 1, 1),
(N'Gà ác Tam Nông', N'Gà ác Tam Nông gói 135-160g/con (1 Vỉ)', 89.000, NULL, 12, 3, 1, 2),
(N'Lươn phi lê', N'Lươn phi lê vỉ 110g (1 Vỉ)', 49.000, NULL, 8, 3, 1, 1),
(N'Táo Mỹ & Nam Phi', N'Táo Mỹ & Nam Phi túi 1.5kg (1 Túi)', 89.000, 98.000, 50, 1, 1, 2);



INSERT INTO product_image (product_id, image_url) VALUES 
(1, 'SanPham1.webp'),
(1, 'SanPham1.1.webp'),
(1, 'SanPham1.2.webp'), -- Hình ảnh cho sản phẩm 1
(2, 'SanPham2.webp'),
(2, 'SanPham2.1.webp'),
(3, 'SanPham3.webp'),
(3, 'SanPham3.1.webp'),
(3, 'SanPham3.2.webp'),
(4, 'SanPham4.webp'),
(4, 'SanPham4.1.webp'),
(4, 'SanPham4.2.webp'),
(4, 'SanPham4.3.webp'),
(4, 'SanPham4.4.webp'),
(5, 'SanPham5.webp'),
(6, 'SanPham6.webp'),
(7, 'SanPham7.webp'),
(8, 'SanPham8.webp'),
(9, 'SanPham9.webp'),
(10, 'SanPham10.webp'),
(11, 'SanPham11.webp'),
(12, 'SanPham12.1.webp'),
(12, 'SanPham12.2.webp'),
(12, 'SanPham12.webp'),
(13, 'SanPham13.webp'),
(14, 'SanPham14.webp'),
(15, 'SanPham15.webp'),
(16, 'SanPham16.webp'),
(17, 'SanPham17.webp'),
(18, 'SanPham18.webp'),
(18, 'SanPham18.1.webp'),
(18, 'SanPham18.2.webp'),
(19, 'SanPham19.webp'),
(20, 'SanPham20.webp'),
(21, 'SanPham21.webp'),
(22, 'SanPham22.webp'),
(22, 'SanPham22.1.webp'),
(23, 'SanPham23.webp'),
(23, 'SanPham23.1.webp'),
(23, 'SanPham23.2.webp'),
(23, 'SanPham23.3.webp'),
(24, 'SanPham24.webp'),
(25, 'SanPham25.webp'),
(26, 'SanPham26.webp'),
(27, 'SanPham27.webp'),
(28, 'SanPham28.webp'),
(28, 'SanPham28.1.webp'),
(29, 'SanPham29.webp'),
(30, 'SanPham30.webp'),
(30, 'SanPham30.1.webp'),
(30, 'SanPham30.2.webp'),
(31, 'SanPham31.webp'),
(31, 'SanPham31.1.webp'),
(32, 'SanPham32.webp'),
(32, 'SanPham32.1.webp'),
(33, 'SanPham33.webp'),
(34, 'SanPham34.webp'),
(35, 'SanPham35.webp'),
(36, 'SanPham36.webp'),
(37, 'SanPham37.webp'),
(38, 'SanPham38.webp'),
(39, 'SanPham39.webp'),
(40, 'SanPham40.webp'),
(41, 'SanPham41.webp'),
(42, 'SanPham42.webp'),
(43, 'SanPham43.webp'),
(44, 'SanPham44.webp'),
(45, 'SanPham45.webp'),
(46, 'SanPham46.webp'),
(47, 'SanPham47.webp'),
(48, 'SanPham48.webp'),
(49, 'SanPham49.webp'),
(50, 'SanPham50.webp'),
(51, 'SanPham51.webp'),
(52, 'SanPham52.webp'),
(53, 'SanPham53.webp'),
(54, 'SanPham54.webp'),
(55, 'SanPham55.webp'),
(56, 'SanPham56.webp'),
(57, 'SanPham57.webp'),
(58, 'SanPham58.webp'),
(59, 'SanPham59.webp'),
(60, 'SanPham60.webp'),
(61, 'SanPham61.webp'),
(62, 'SanPham62.webp'),
(63, 'SanPham63.webp'),
(64, 'SanPham64.webp'),
(65, 'SanPham65.webp'),
(66, 'SanPham66.webp'),
(67, 'SanPham67.webp'),
(68, 'SanPham68.webp'),
(69, 'SanPham69.webp'),
(70, 'SanPham70.webp'),
(71, 'SanPham71.webp'),
(72, 'SanPham72.webp'),
(73, 'SanPham73.webp'),
(74, 'SanPham74.webp'),
(75, 'SanPham75.webp'),
(76, 'SanPham76.webp'),
(77, 'SanPham77.webp'),
(78, 'SanPham78.webp'),
(79, 'SanPham79.webp'),
(80, 'SanPham80.webp'),
(81, 'SanPham81.webp'),
(82, 'SanPham82.webp'),
(83, 'SanPham83.webp'),
(84, 'SanPham84.webp'),
(85, 'SanPham85.webp'),
(86, 'SanPham86.webp'),
(87, 'SanPham87.webp'),
(88, 'SanPham88.webp'),
(89, 'SanPham89.webp'),
(90, 'SanPham90.webp'),
(91, 'SanPham91.webp'),
(92, 'SanPham92.webp'),
(93, 'SanPham93.webp'),
(94, 'SanPham94.webp'),
(95, 'SanPham95.webp'),
(96, 'SanPham96.webp'),
(97, 'SanPham97.webp'),
(98, 'SanPham98.webp'),
(98, 'SanPham98.1.webp'),
(99, 'SanPham99.webp'),
(100, 'SanPham100.webp');



INSERT INTO orders (account_id, order_date, payment_status, payment_method) VALUES 
(1, GETDATE(), '2', 'cash'),  -- Đơn hàng của User 1
(2, GETDATE(), '1', 'bank transfer'); -- Đơn hàng của User 2



INSERT INTO order_detail (order_id, product_id, quantity, price) VALUES 
(1, 1, 2, 100), -- Chi tiết cho đơn hàng 1
(2, 1, 1, 100); -- Chi tiết cho đơn hàng 2




INSERT INTO Cart (account_id, product_id, quantity, price) VALUES 
(1, 1, 2, 298.000), -- Sản phẩm 1 trong giỏ hàng của User 1
(1, 2, 1, 69.000),  -- Sản phẩm 2 trong giỏ hàng của User 1
(2, 3, 3, 300.000), -- Sản phẩm 3 trong giỏ hàng của User 2
(2, 4, 1, 98.000);  -- Sản phẩm 4 trong giỏ hàng của User 2