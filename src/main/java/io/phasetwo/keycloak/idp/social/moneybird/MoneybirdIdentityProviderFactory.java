package io.phasetwo.keycloak.idp.social.moneybird;

import com.google.auto.service.AutoService;
import org.keycloak.broker.oidc.OAuth2IdentityProviderConfig;
import org.keycloak.broker.provider.AbstractIdentityProviderFactory;
import org.keycloak.broker.social.SocialIdentityProviderFactory;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.KeycloakSession;

/** */
@AutoService(SocialIdentityProviderFactory.class)
public class MoneybirdIdentityProviderFactory
    extends AbstractIdentityProviderFactory<MoneybirdIdentityProvider>
    implements SocialIdentityProviderFactory<MoneybirdIdentityProvider> {

  public static final String PROVIDER_ID = "moneybird";

  @Override
  public String getName() {
    return "Moneybird";
  }

  @Override
  public MoneybirdIdentityProvider create(KeycloakSession session, IdentityProviderModel model) {
    return new MoneybirdIdentityProvider(session, new OAuth2IdentityProviderConfig(model));
  }

  @Override
  public OAuth2IdentityProviderConfig createConfig() {
    return new OAuth2IdentityProviderConfig();
  }

  @Override
  public String getId() {
    return PROVIDER_ID;
  }
}
