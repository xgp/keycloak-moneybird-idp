package io.phasetwo.keycloak.idp.social.moneybird;

import com.google.auto.service.AutoService;
import org.keycloak.broker.oidc.mappers.AbstractJsonUserAttributeMapper;
import org.keycloak.broker.provider.IdentityProviderMapper;

/** */
@AutoService(IdentityProviderMapper.class)
public class MoneybirdUserAttributeMapper extends AbstractJsonUserAttributeMapper {

  public static final String PROVIDER_ID = "moneybird-user-attribute-mapper";
  private static final String[] cp = new String[] {MoneybirdIdentityProviderFactory.PROVIDER_ID};

  @Override
  public String[] getCompatibleProviders() {
    return cp;
  }

  @Override
  public String getId() {
    return PROVIDER_ID;
  }
}
