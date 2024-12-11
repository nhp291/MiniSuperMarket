package devmagic.Controller.Admin;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/Signin")
public class Gg_Fb_loginController {

    @GetMapping("/google")
    public Map<String, Object> currentUser(OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        if (oAuth2AuthenticationToken == null) {
            throw new IllegalStateException("OAuth2 Authentication token is null.");
        }

        // Check if the token is valid
        return oAuth2AuthenticationToken.getPrincipal().getAttributes();
    }
}

