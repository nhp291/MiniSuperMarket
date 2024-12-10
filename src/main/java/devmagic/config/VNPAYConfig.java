package devmagic.config;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class VNPAYConfig {
    public static String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static String vnp_ReturnUrl = "http://yourwebsite.com/vnpay-return"; // Cập nhật URL chính xác
    public static String vnp_TmnCode = "DKXB91V1";
    public static String secretKey = "U4LRHZQ4GJ8S2L5QSLD3ZDY7SE388L8O";
    public static String vnp_ApiUrl = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";

    public static String hmacSHA512(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA512");
            mac.init(secret_key);
            byte[] bytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hash = new StringBuilder();
            for (byte b : bytes) {
                hash.append(String.format("%02x", b));
            }
            return hash.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate HMACSHA512", e);
        }
    }

    public static String createPaymentUrl(double totalPrice, String description) {
        String vnp_TxnRef = getRandomNumber(8); // Mã giao dịch
        String vnp_OrderInfo = description;
        String vnp_Amount = String.valueOf((int) (totalPrice * 100)); // VNPay yêu cầu số tiền tính bằng VND (nhân 100)

        Map<String, String> params = new HashMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", vnp_TmnCode);
        params.put("vnp_Amount", vnp_Amount);
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", vnp_TxnRef);
        params.put("vnp_OrderInfo", vnp_OrderInfo);
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        params.put("vnp_IpAddr", "127.0.0.1"); // Hoặc lấy từ request.getRemoteAddr()
        params.put("vnp_CreateDate", getCurrentDateTime());

        // Tính secure hash
        String queryUrl = createQueryString(params);
        String secureHash = hmacSHA512(secretKey, queryUrl);
        queryUrl += "&vnp_SecureHash=" + secureHash;

        return vnp_PayUrl + "?" + queryUrl;
    }

    public static String getCurrentDateTime() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }

    public static String createQueryString(Map<String, String> params) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        StringBuilder queryString = new StringBuilder();
        for (Iterator<String> itr = fieldNames.iterator(); itr.hasNext(); ) {
            String key = itr.next();
            String value = params.get(key);
            try {
                queryString.append(URLEncoder.encode(key, StandardCharsets.US_ASCII.toString()))
                        .append('=')
                        .append(URLEncoder.encode(value, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    queryString.append('&');
                }
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Error encoding URL parameters", e);
            }
        }
        return queryString.toString();
    }

    public static String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
