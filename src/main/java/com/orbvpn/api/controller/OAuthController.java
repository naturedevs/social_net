package com.orbvpn.api.controller;

import com.orbvpn.api.domain.dto.AuthenticatedUser;
import com.orbvpn.api.domain.enums.SocialMedia;
import com.orbvpn.api.service.social_login.OauthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class OAuthController {
    @Autowired
    private OauthService oauthService;

//    @GetMapping("/oauth2/callback/google")
//    public AuthenticatedUser google(@RequestParam("code") String code) {
//      System.out.println("OAuthController google : code = " + code);
//
//      return this.oauthService.getTokenAndLogin(code, SocialMedia.GOOGLE);
//    }
//
    @GetMapping("/oauth2/callback/github")
    public AuthenticatedUser github(@RequestParam("code") String code, @RequestParam("state") String state) {
        System.out.println("OAuthController github : code = " + code + " : state = " + state);
        Authentication authentication = new Authentication();
        SecurityContextHolder.getContext().setAuthentication();
        return this.oauthService.getTokenAndLogin(code, SocialMedia.GITHUB);
    }
//
//    @GetMapping("/oauth2/callback/facebook")
//    public void facebook(@RequestParam("code") String code) {
//      System.out.println("OAuthController facebook : code = " + code);
//
//      this.oauthService.getTokenAndLogin(code, SocialMedia.FACEBOOK);
//    }
//
//    @GetMapping("/oauth2/callback/linkedin")
//    public AuthenticatedUser linkedIn(@RequestParam("code") String code) {
//      System.out.println("OAuthController linkedIn : code = " + code);
//
//      return this.oauthService.getTokenAndLogin(code, SocialMedia.LINKEDIN);
//    }
//
//    @GetMapping("/oauth2/authorization/manual/twitter")
//    public void twitterOauthLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        System.out.println("twitterOauthLogin");
//        String authorizeUrl = oauthService.twitterOauthLogin();
//        response.sendRedirect(authorizeUrl);
//    }
//
//    @GetMapping("/oauth2/callback/twitter")
//    public AuthenticatedUser getTwitter(HttpServletRequest request, HttpServletResponse response) {
//      System.out.println("OAuthController getTwitter : ");
//
//      return oauthService.twitterUserProfile(request, response);
//    }
}
