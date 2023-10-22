package com.orbvpn.api.service.social_login;

import com.orbvpn.api.domain.dto.OAuthToken;
import org.apache.poi.util.NotImplemented;
import org.hibernate.mapping.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

import static com.orbvpn.api.domain.OAuthConstants.*;

@Service
public class TokenService {

    private RestTemplate restTemplate;
    private HttpHeaders headers;
    private UriComponentsBuilder builder;



    @NotImplemented
    public String getAmazonToken(String code) {
        return null;
    }
    @NotImplemented
    public String getGithubToken(String code) {
        System.out.println("TokenService getGithubToken : code = " + code);

//        RestTemplate restTemplate = new RestTemplate();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBasicAuth("Iv1.731596c579d3b990", "1a16900f622b3b74d6d2e1efa40c53a63ad8705d");
//
//        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
//        map.add("grant_type", "authorization_code");
//        map.add("code", code);
//        map.add("redirect_uri", "http://localhost/oauth2/callback/github");
//
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
//
//        ResponseEntity<String> response = restTemplate.exchange("https://github.com/login/oauth/access_token", HttpMethod.POST, request, String.class);
//        System.out.println(response.getBody());

//        return Objects.requireNonNull(response.getBody()).getAccess_token();
//return null;
        restTemplate = new RestTemplate();
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        builder = UriComponentsBuilder.fromHttpUrl("https://github.com/login/oauth/access_token")
            .queryParam("code",code)
            .queryParam("client_id","Iv1.731596c579d3b990")
            .queryParam("client_secret","1a16900f622b3b74d6d2e1efa40c53a63ad8705d")
            .queryParam("redirect_uri","http://localhost/oauth2/callback/github")
            .queryParam("response_type","token");

        HttpEntity<?> entity = new HttpEntity<>(headers);

        HttpEntity<OAuthToken> response = restTemplate.exchange(
            builder.toUriString(),
            HttpMethod.POST,
            entity,
            OAuthToken.class);
        System.out.println(response);
        System.out.println("TokenService getGithubToken end ");

        return Objects.requireNonNull(response.getBody()).getAccess_token();

//        return null;
    }
    public String getLinkedinToken(String code) {
        restTemplate = new RestTemplate();
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        builder = UriComponentsBuilder.fromHttpUrl(linkedinTokenURL)
                .queryParam("code",code)
                .queryParam("client_id",linkedinClientId)
                .queryParam("client_secret",linkedinClientSecret)
                .queryParam("redirect_uri",linkedinRedirectURL)
                .queryParam("grant_type","authorization_code");

        HttpEntity<?> entity = new HttpEntity<>(headers);

        HttpEntity<OAuthToken> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.POST,
                entity,
                OAuthToken.class);

        return Objects.requireNonNull(response.getBody()).getAccess_token();
    }
    @NotImplemented
    public String getAppleToken(String code) {
        return null;
    }

    @NotImplemented
    public String getFacebookToken(String code) {
        return null;
    }

    public String getGoogleToken(String code) {

        restTemplate = new RestTemplate();
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        builder = UriComponentsBuilder.fromHttpUrl(googleTokenURL)
                .queryParam("code",code)
                .queryParam("client_id",googleClientId)
                .queryParam("client_secret",googleClientSecret)
                .queryParam("redirect_uri",googleRedirectURL)
                .queryParam("grant_type","authorization_code");

        HttpEntity<?> entity = new HttpEntity<>(headers);

        HttpEntity<OAuthToken> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.POST,
                entity,
                OAuthToken.class);

        return Objects.requireNonNull(response.getBody()).getId_token();
    }
}
