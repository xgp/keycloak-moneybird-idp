package io.phasetwo.keycloak.idp.social.moneybird;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.broker.oidc.AbstractOAuth2IdentityProvider;
import org.keycloak.broker.oidc.OAuth2IdentityProviderConfig;
import org.keycloak.broker.oidc.mappers.AbstractJsonUserAttributeMapper;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.provider.IdentityBrokerException;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.broker.social.SocialIdentityProvider;
import org.keycloak.events.EventBuilder;
import org.keycloak.models.KeycloakSession;

/** */
@JBossLog
public class MoneybirdIdentityProvider extends AbstractOAuth2IdentityProvider
    implements SocialIdentityProvider {

  public static final String AUTH_URL = "https://moneybird.com/oauth/authorize";
  public static final String TOKEN_URL = "https://moneybird.com/oauth/token";
  public static final String DEFAULT_SCOPE = "sales_invoices";

  private static final String ADMINISTRATIONS_URL =
      "https://moneybird.com/api/v2/administrations.json";
  private static final String PROFILE_URL_TEMPLATE =
      "https://moneybird.com/api/v2/%s/users/userinfo.json";

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
  protected BrokeredIdentityContext extractIdentityFromProfile(
      EventBuilder event, JsonNode profile) {
    log.infof("profile %s", jsonString(profile));

    BrokeredIdentityContext user = new BrokeredIdentityContext(getJsonProperty(profile, "id"));

    String username = getJsonProperty(profile, "email");
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
    String profileUrl = String.format(PROFILE_URL_TEMPLATE, getAdministration(accessToken));
    try {
      JsonNode profile =
          SimpleHttp.doGet(profileUrl, session)
              .header("Authorization", "Bearer " + accessToken)
              .asJson();
      BrokeredIdentityContext user = extractIdentityFromProfile(null, profile);
      return user;
    } catch (Exception e) {
      throw new IdentityBrokerException("Could not obtain user profile from moneybird.", e);
    }
  }

  /**
   * This is a hack suggested by the moneybird developers. You need an administration_id in order to
   * query their userinfo endpoint. They suggested calling the administrations endpoint and just
   * using the first one. TBD will this work for multiple administrations, and if we don't have
   * access to the first?
   */
  private String getAdministration(String accessToken) {
    try {
      JsonNode admins =
          SimpleHttp.doGet(ADMINISTRATIONS_URL, session)
              .header("Authorization", "Bearer " + accessToken)
              .asJson();
      return admins.get(0).get("id").textValue();
    } catch (Exception e) {
      throw new IdentityBrokerException("Could not obtain administrations from moneybird.", e);
    }
  }

  @Override
  protected String getDefaultScopes() {
    return DEFAULT_SCOPE;
  }

  private static String jsonString(JsonNode jsonNode) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      Object json = mapper.readValue(jsonNode.toString(), Object.class);
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
    } catch (Exception e) {
      return null;
    }
  }
}
