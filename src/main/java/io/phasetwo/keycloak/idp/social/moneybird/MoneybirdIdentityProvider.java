package io.phasetwo.keycloak.idp.social.moneybird;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.Iterator;
import org.keycloak.broker.oidc.AbstractOAuth2IdentityProvider;
import org.keycloak.broker.oidc.OAuth2IdentityProviderConfig;
import org.keycloak.broker.oidc.mappers.AbstractJsonUserAttributeMapper;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.provider.IdentityBrokerException;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.broker.social.SocialIdentityProvider;
import org.keycloak.events.EventBuilder;
import org.keycloak.models.KeycloakSession;
import lombok.extern.jbosslog.JBossLog;

/**
 */
@JBossLog
public class MoneybirdIdentityProvider extends AbstractOAuth2IdentityProvider
    implements SocialIdentityProvider {

  public static final String AUTH_URL = "https://moneybird.com/oauth/authorize";
  public static final String TOKEN_URL = "https://moneybird.com/oauth/token";
  public static final String DEFAULT_SCOPE = "sales_invoices";

  public MoneybirdIdentityProvider(KeycloakSession session, OAuth2IdentityProviderConfig config) {
    super(session, config);
    config.setAuthorizationUrl(AUTH_URL);
    config.setTokenUrl(TOKEN_URL);
  }

  @Override
  protected boolean supportsExternalExchange() {
    return true;
  }

  @Override
  protected BrokeredIdentityContext doGetFederatedIdentity(String accessToken) {
    log.infof("doGetFederatedIdentity(%s)", accessToken);
    return null;
  }
    
  /*
  @Override
  protected String getProfileEndpointForValidation(EventBuilder event) {
    return PROFILE_URL;
  }

  @Override
  protected BrokeredIdentityContext extractIdentityFromProfile(
      EventBuilder event, JsonNode profile) {
    BrokeredIdentityContext user = new BrokeredIdentityContext(getJsonProperty(profile, "id"));

    String username = getJsonProperty(profile, "login");
    user.setUsername(username);
    user.setName(getJsonProperty(profile, "name"));
    user.setEmail(getJsonProperty(profile, "email"));
    user.setIdpConfig(getConfig());
    user.setIdp(this);

    AbstractJsonUserAttributeMapper.storeUserProfileForMapper(
        user, profile, getConfig().getAlias());

    return user;
  }

  @Override
  protected BrokeredIdentityContext doGetFederatedIdentity(String accessToken) {
    try {
      JsonNode profile =
          SimpleHttp.doGet(PROFILE_URL, session)
              .header("Authorization", "Bearer " + accessToken)
              .asJson();

      BrokeredIdentityContext user = extractIdentityFromProfile(null, profile);

      if (user.getEmail() == null) {
        user.setEmail(searchEmail(accessToken));
      }

      return user;
    } catch (Exception e) {
      throw new IdentityBrokerException("Could not obtain user profile from moneybird.", e);
    }
  }

  private String searchEmail(String accessToken) {
    try {
      ArrayNode emails =
          (ArrayNode)
              SimpleHttp.doGet(EMAIL_URL, session)
                  .header("Authorization", "Bearer " + accessToken)
                  .asJson();

      Iterator<JsonNode> loop = emails.elements();
      while (loop.hasNext()) {
        JsonNode mail = loop.next();
        if (mail.get("primary").asBoolean()) {
          return getJsonProperty(mail, "email");
        }
      }
    } catch (Exception e) {
      throw new IdentityBrokerException("Could not obtain user email from moneybird.", e);
    }
    throw new IdentityBrokerException("Primary email from moneybird is not found.");
  }
  */
  
  @Override
  protected String getDefaultScopes() {
    return DEFAULT_SCOPE;
  }
}
