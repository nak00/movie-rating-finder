package com.example.movieratingfinder.security;

import com.example.movieratingfinder.model.User;
import com.example.movieratingfinder.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;

    // Constructor injection instead of field injection
    public OAuth2LoginSuccessHandler(UserService userService) {
        this.userService = userService;
        setDefaultTargetUrl("/");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = token.getPrincipal();

        String provider = token.getAuthorizedClientRegistrationId();
        String providerId = oAuth2User.getAttribute("sub"); // For Google

        // Facebook uses "id" instead of "sub"
        if (providerId == null) {
            providerId = oAuth2User.getAttribute("id");
        }

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // Process user in our system
        User user = userService.processOAuthUser(provider, providerId, email, name);

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
