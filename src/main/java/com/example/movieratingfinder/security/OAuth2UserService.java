package com.example.movieratingfinder.security;

import com.example.movieratingfinder.service.UserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    // Constructor injection instead of field injection
    public OAuth2UserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // Extract provider details
        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerId = oAuth2User.getAttribute("sub"); // For Google

        // Facebook uses "id" instead of "sub"
        if (providerId == null) {
            providerId = oAuth2User.getAttribute("id");
        }

        // Extract email and name
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // Process user in our system
        userService.processOAuthUser(provider, providerId, email, name);

        return oAuth2User;
    }
}
