package devmagic.Controller.User;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @GetMapping("/getAccountId")
    public ResponseEntity<Map<String, Integer>> getAccountId(HttpSession session, HttpServletRequest request) {
        // Kiểm tra thông tin từ cookie
        Integer accountId = getAccountIdFromCookies(request);
        Map<String, Integer> response = new HashMap<>();

        if (accountId != null) {
            response.put("accountId", accountId);
            return ResponseEntity.ok(response);
        } else {
            response.put("error", -1); // Trả về -1 nếu không tìm thấy accountId
            return ResponseEntity.status(404).body(response);
        }
    }

    private Integer getAccountIdFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accountId".equals(cookie.getName())) {
                    try {
                        return Integer.parseInt(cookie.getValue());
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }
}
