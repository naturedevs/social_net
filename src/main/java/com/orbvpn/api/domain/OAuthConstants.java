package com.orbvpn.api.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OAuthConstants {

    public static String googleClientId;
    public static String googleTokenURL;
    public static String googleRedirectURL;
    public static String googleClientSecret;

    public static String facebookAppId;
    public static String facebookAppSecret;

    public static String twitterClientId;
    public static String twitterClientSecret;
    public static String twitterCallbackUrl;
    public static String twitterUserInfoUrl;

    public static String linkedinClientId;
    public static String linkedinClientSecret;
    public static String linkedinRedirectURL;
    public static String linkedinTokenURL;
    public static String linkedinProfileURL;
    public static String linkedinEmailURL;

    @Value("${oauth.google.client-id}")
    public void setGoogleClientId(String _googleClientId){
        googleClientId = _googleClientId;
    }

    @Value("${oauth.google.token-url}")
    public void setGoogleTokenURL(String _googleTokenURL){
        googleTokenURL = _googleTokenURL;
    }

    @Value("${oauth.google.client-secret}")
    public void setGoogleClientSecret(String _googleClientSecret){
        googleClientSecret = _googleClientSecret;
    }

    @Value("${oauth.google.redirectUri}")
    public void setGoogleRedirectURL(String _googleRedirectURL){
        googleRedirectURL = _googleRedirectURL;
    }

    @Value("${oauth.facebook.app-id}")
    public void setFacebookAppId(String _facebookAppId){
        facebookAppId = _facebookAppId;
    }

    @Value("${oauth.facebook.app-secret}")
    public void setFacebookAppSecret(String _facebookAppSecret){
        facebookAppSecret = _facebookAppSecret;
    }

    @Value("${oauth.twitter.client-id}")
    public void setTwitterClientId(String _twitterClientId){
        twitterClientId = _twitterClientId;
    }

    @Value("${oauth.twitter.client-secret}")
    public void setTwitterClientSecret(String _twitterClientSecret){
        twitterClientSecret = _twitterClientSecret;
    }

    @Value("${oauth.twitter.callbackUrl}")
    public void setTwitterCallbackUrl(String _twitterCallbackUrl){
        twitterCallbackUrl = _twitterCallbackUrl;
    }

    @Value("${oauth.twitter.userInfoUrl}")
    public void setTwitterUserInfoUrl(String _twitterUserInfoUrl){
        twitterUserInfoUrl = _twitterUserInfoUrl;
    }

    @Value("${oauth.linkedin.client-id}")
    public void setLinkedinClientId(String _linkedinClientId){
        linkedinClientId = _linkedinClientId;
    }

    @Value("${oauth.linkedin.client-secret}")
    public void setLinkedinClientSecret(String _linkedinClientSecret){
        linkedinClientSecret = _linkedinClientSecret;
    }

    @Value("${oauth.linkedin.token-url}")
    public void setLinkedinTokenURL(String _linkedinTokenURL){
        linkedinTokenURL = _linkedinTokenURL;
    }

    @Value("${oauth.linkedin.redirectUri}")
    public void setLinkedinRedirectURL(String _linkedinRedirectURL){
        linkedinRedirectURL = _linkedinRedirectURL;
    }

    @Value("${oauth.linkedin.profile-url}")
    public void setLinkedinProfileURL(String _linkedinProfileURL){
        linkedinProfileURL = _linkedinProfileURL;
    }

    @Value("${oauth.linkedin.email-url}")
    public void setLinkedinEmailURL(String _linkedinEmailURL){
        linkedinEmailURL = _linkedinEmailURL;
    }


}
