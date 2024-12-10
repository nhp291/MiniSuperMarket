package devmagic.Controller.User;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import devmagic.Dto.CartRequest;
import devmagic.config.VNPAYConfig;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

@RestController
public class PaymentController {

    /**
     * Xử lý kết quả trả về từ VNPay sau thanh toán
     */
    @GetMapping("/vnpay-return")
    public String handleVNPayReturn(HttpServletRequest request, Model model) {
        Map<String, String> params = new HashMap<>();
        request.getParameterMap().forEach((key, values) -> params.put(key, values[0]));

        String vnp_SecureHash = params.remove("vnp_SecureHash");
        String secureHash = VNPAYConfig.hmacSHA512(VNPAYConfig.secretKey, VNPAYConfig.createQueryString(params));

        if (vnp_SecureHash != null && vnp_SecureHash.equals(secureHash)) {
            // Thanh toán thành công
            model.addAttribute("message", "Thanh toán thành công!");
        } else {
            // Thanh toán thất bại
            model.addAttribute("message", "Thanh toán thất bại, chữ ký không hợp lệ!");
        }

        return "payment-result";
    }

    /**
     * Tạo URL thanh toán và chuyển hướng đến VNPay
     */
    @PostMapping("/pay")
    public String redirectToVNPay(@RequestBody CartRequest cartRequest) {
        try {
            BigDecimal totalPrice = cartRequest.getTotalPrice();
            String paymentUrl = VNPAYConfig.createPaymentUrl(totalPrice.doubleValue(), "Thanh toán giỏ hàng");

            return paymentUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/cart?error=payment_failed";
        }
    }

    /**
     * Tạo URL thanh toán VNPay trực tiếp
     */
    @GetMapping("/pay")
    public String getPay() throws UnsupportedEncodingException {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        long amount = 10000 * 100;
        String bankCode = "NCB";

        String vnp_TxnRef = VNPAYConfig.getRandomNumber(8);
        String vnp_IpAddr = "127.0.0.1";

        String vnp_TmnCode = VNPAYConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", bankCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toán đơn hàng:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VNPAYConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && fieldValue.length() > 0) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()))
                        .append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                query.append('&');
                hashData.append('&');
            }
        }

        String queryUrl = query.toString();
        String vnp_SecureHash = VNPAYConfig.hmacSHA512(VNPAYConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VNPAYConfig.vnp_PayUrl + "?" + queryUrl;

        return paymentUrl;
    }
}
