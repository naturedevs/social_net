package com.orbvpn.api.service.social_login;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.orbvpn.api.domain.dto.*;
import com.orbvpn.api.domain.entity.Role;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.domain.enums.RoleName;
import com.orbvpn.api.domain.enums.SocialMedia;
import com.orbvpn.api.exception.OauthLoginException;
import com.orbvpn.api.reposiitory.UserRepository;
import com.orbvpn.api.service.PasswordService;
import com.orbvpn.api.service.RoleService;
import com.orbvpn.api.service.UserService;
import com.orbvpn.api.service.reseller.ResellerService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.NotImplemented;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1Parameters;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.interfaces.RSAPublicKey;
import java.text.MessageFormat;
import java.util.Collections;

import static com.orbvpn.api.domain.OAuthConstants.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class OauthService {

  private static final HttpTransport transport = new NetHttpTransport();
  private static final JsonFactory jsonFactory = new GsonFactory();

  private final RoleService roleService;
  private final UserService userService;
  private final PasswordService passwordService;
  private final ResellerService resellerService;
  private final UserRepository userRepository;
  private final TokenService tokenService;

  public AuthenticatedUser oauthLogin(String token, SocialMedia socialMedia) {
    System.out.println("OauthService oauthLogin token : " + token);

    TokenData tokenData = getTokenData(token, socialMedia);

    User user = userRepository.findByUsername(tokenData.getEmail())
      .orElseGet(() -> createUser(tokenData));

    return userService.loginInfo(user);
  }

  public AuthenticatedUser getTokenAndLogin(String code, SocialMedia socialMedia){
    System.out.println("OauthService getTokenAndLogin");
    String token;

    switch (socialMedia) {
      case GOOGLE:
        token = tokenService.getGoogleToken(code);
        break;
      case FACEBOOK:
        token = tokenService.getFacebookToken(code);
        break;
      case APPLE:
        token = tokenService.getAppleToken(code);
        break;
      case LINKEDIN:
        token = tokenService.getLinkedinToken(code);
        break;
      case AMAZON:
        token = tokenService.getAmazonToken(code);
        break;
      case GITHUB:
        token = tokenService.getGithubToken(code);
        break;
      default:
        throw new OauthLoginException("Unknown Token provider.");
    }

    return oauthLogin(token, socialMedia);
  }

  public TokenData getTokenData(String token, SocialMedia socialMedia) {
    System.out.println("OauthService getTokenData token : " + token);

    switch (socialMedia) {
      case GOOGLE:
        return getGoogleTokenData(token);
      case FACEBOOK:
        return getFacebookTokenData(token);
      case APPLE:
        return getAppleTokenData(token);
      case LINKEDIN:
        return getLinkedinTokenData(token);
      case AMAZON:
        return getAmazonTokenData(token);
      case GITHUB:
        return getGithubTokenData(token);
      default:
        throw new OauthLoginException();
    }
  }

  private User createUser(TokenData tokenData) {
    String password = userService.generateRandomString();

    User user = new User();
    user.setUsername(tokenData.getEmail());
    user.setEmail(tokenData.getEmail());
    passwordService.setPassword(user, password);
    Role role = roleService.getByName(RoleName.USER);
    user.setRole(role);
    user.setReseller(resellerService.getOwnerReseller());
    user.setOauthId(tokenData.getOauthId());

    userRepository.save(user);

    userService.assignTrialSubscription(user);

    return user;
  }

  private TokenData getGoogleTokenData(String token) {
    GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
      .setAudience(Collections.singletonList(googleClientId))
      .build();

    GoogleIdToken idTokenData;
    try {
      idTokenData = verifier.verify(token);
    } catch (Exception ex) {
      log.error(String.format("Exception occurred while verifying Google user token : %s",ex.getCause() ));
      throw new OauthLoginException();
    }

    if (idTokenData == null) {
      log.error("idTokenData is null");
      throw new OauthLoginException();
    }

    Payload payload = idTokenData.getPayload();

    String email = payload.getEmail();
    long exp = payload.getExpirationTimeSeconds();
    long iat = payload.getIssuedAtTimeSeconds();

    return TokenData.builder()
      .email(email)
      .exp(exp)
      .iat(iat)
      .oauthId("GOOGLE")
      .build();
  }

  private TokenData getFacebookTokenData(String token) {
    RestTemplate debugRequest = new RestTemplate();
    String debugUrl = MessageFormat.format("https://graph.facebook.com/debug_token?input_token={0}&access_token={1}|{2}",token, facebookAppId, facebookAppSecret);
    ResponseEntity<FBTokenMetadataWrapper> fbTokenMetadataWrapperResponse = debugRequest.getForEntity(debugUrl, FBTokenMetadataWrapper.class);

    FBTokenMetadata fbTokenMetadata = fbTokenMetadataWrapperResponse.getBody().getData();

    if(fbTokenMetadata == null) {
      throw new OauthLoginException();
    }

    String appId = fbTokenMetadata.getAppId();

    if(appId == null || !appId.equals(facebookAppId)) {
      throw new OauthLoginException();
    }

    RestTemplate dataRequest = new RestTemplate();
    String dataUrl = MessageFormat.format("https://graph.facebook.com/me?fields=email&access_token={0}", token);
    ResponseEntity<FBTokenData> fbTokenDataResponse = dataRequest.getForEntity(dataUrl, FBTokenData.class);
    FBTokenData fbTokenData = fbTokenDataResponse.getBody();

    return TokenData.builder()
      .email(fbTokenData.getEmail())
      .oauthId(fbTokenData.getId())
      .build();

  }

  private TokenData getAppleTokenData(String encryptedToken) {
    try {
      DecodedJWT jwt = JWT.decode(encryptedToken);

      JwkProvider provider = new UrlJwkProvider("https://appleid.apple.com/auth/keys");
      Jwk jwk = provider.get(jwt.getKeyId());
      Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
      algorithm.verify(jwt);

      Claims claims = Jwts.parserBuilder().setSigningKey((RSAPublicKey)
        jwk.getPublicKey()).build().parseClaimsJws(encryptedToken).getBody();


      return TokenData.builder()
        .email(claims.get("email", String.class))
        .build();
    } catch (Exception ex) {
      throw new OauthLoginException(ex.getMessage());
    }
  }

  private TokenData getLinkedinTokenData(String encryptedToken) {

    String email;

    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();

    UriComponentsBuilder     builder = UriComponentsBuilder.fromHttpUrl(linkedinEmailURL)
            .queryParam("oauth2_access_token",encryptedToken)
            .queryParam("q","members")
            .queryParam("projection","(elements*(primary,type,handle~))");

    HttpEntity<?> entity = new HttpEntity<>(headers);

    try{

      HttpEntity<ObjectNode> response = restTemplate.exchange(
              builder.toUriString(),
              HttpMethod.GET,
              entity,
              ObjectNode.class);

      email = response.getBody().get("elements").get(0).get("handle~").get("emailAddress").asText();
    } catch (Exception ex) {

      throw new OauthLoginException(ex.getMessage());
    }

    return TokenData.builder()
            .email(email)
            .oauthId("LINKEDIN")
            .build();
  }

  @NotImplemented
  private TokenData getAmazonTokenData(String encryptedToken) {
    try {
      return null;
    } catch (Exception ex) {
      throw new OauthLoginException(ex.getMessage());
    }
  }

  @NotImplemented
  private TokenData getGithubTokenData(String encryptedToken) {
    System.out.println("OauthService getGithubTokenData encryptedToken : " + encryptedToken);

    RestTemplate restTemplate = new RestTemplate();

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "token " + encryptedToken);

    HttpEntity<?> entity = new HttpEntity<>("parameters", headers);

    HttpEntity<ObjectNode> response = restTemplate.exchange("https://api.github.com/user", HttpMethod.GET, entity, ObjectNode.class);

    String email = response.getBody().get("login").asText();

    return TokenData.builder()
        .email(email + "@github.com")
        .oauthId("GITHUB")
        .build();
  }

  public String twitterOauthLogin(){

    TwitterConnectionFactory connectionFactory = new TwitterConnectionFactory( twitterClientId,twitterClientSecret );
    OAuth1Operations oauthOperations = connectionFactory.getOAuthOperations();
    OAuthToken requestToken = oauthOperations.fetchRequestToken( twitterCallbackUrl, null );

    return oauthOperations.buildAuthorizeUrl(requestToken.getValue(), OAuth1Parameters.NONE);
  }

  public AuthenticatedUser twitterUserProfile(HttpServletRequest request, HttpServletResponse response){

    TwitterConnectionFactory connectionFactory = new TwitterConnectionFactory( twitterClientId,twitterClientSecret );
    OAuth1Operations oauthOperations = connectionFactory.getOAuthOperations();
    OAuthToken oAuthToken=new OAuthToken(request.getParameter("oauth_token"),request.getParameter("oauth_verifier"));

    OAuthToken accessToken = oauthOperations.exchangeForAccessToken(new AuthorizedRequestToken(oAuthToken,request.getParameter("oauth_verifier")), null);

    TwitterTemplate twitterTemplate = new TwitterTemplate( twitterClientId, twitterClientSecret, accessToken.getValue(), accessToken.getSecret() );

    RestTemplate restTemplate = twitterTemplate.getRestTemplate();
    ObjectNode objectNode = restTemplate.getForObject(twitterUserInfoUrl, ObjectNode.class);

    String email;
    try{
      email = objectNode.get("email").asText();
    } catch (NullPointerException exception){
      email = objectNode.get("screen_name").asText();
      log.error("Could not retrieve user email.");
    }

    TokenData tokenData = TokenData.builder()
            .email(email)
            .oauthId("TWITTER")
            .build();

    User user = userRepository.findByUsername(email)
            .orElseGet(() -> createUser(tokenData));

    return userService.loginInfo(user);

  }


}
