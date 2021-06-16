package com.github.thomasdarimont.keycloak.custom.mfa.sms;

import com.github.thomasdarimont.keycloak.custom.mfa.sms.client.SmsClientFactory;
import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.provider.ServerInfoAwareProviderFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@AutoService(AuthenticatorFactory.class)
public class SmsAuthenticatorFactory implements AuthenticatorFactory, ServerInfoAwareProviderFactory {

    public static final SmsAuthenticator INSTANCE = new SmsAuthenticator();

    private static final List<ProviderConfigProperty> CONFIG_PROPERTIES;

    static {
        List<ProviderConfigProperty> list = ProviderConfigurationBuilder
                .create()
                .property().name(SmsAuthenticator.CONFIG_CODE_LENGTH)
                .type(ProviderConfigProperty.STRING_TYPE)
                .label("Code length")
                .defaultValue(8)
                .helpText("The length of the generated Code.")
                .add()
                .property().name(SmsAuthenticator.CONFIG_CODE_TTL)
                .type(ProviderConfigProperty.STRING_TYPE)
                .label("Time-to-live")
                .defaultValue("300")
                .helpText("The time to live in seconds for the code to be valid.")
                .add()
                .property().name(SmsAuthenticator.CONFIG_SENDER)
                .type(ProviderConfigProperty.STRING_TYPE)
                .label("Sender")
                .defaultValue("$realmDisplayName")
                .helpText("Denotes the message sender of the SMS. Defaults to $realmDisplayName")
                .add()
                .property().name(SmsAuthenticator.CONFIG_CLIENT)
                .type(ProviderConfigProperty.LIST_TYPE)
                .options(SmsClientFactory.MOCK_CLIENT)
                .label("Client")
                .defaultValue(SmsClientFactory.MOCK_CLIENT)
                .helpText("Denotes the client to send the SMS")
                .add()
                .build();

        CONFIG_PROPERTIES = Collections.unmodifiableList(list);
    }

    @Override
    public String getId() {
        return "acme-sms-authenticator";
    }

    @Override
    public String getDisplayType() {
        return "SMS Authentication";
    }

    @Override
    public String getHelpText() {
        return "Validates a code sent via SMS.";
    }

    @Override
    public String getReferenceCategory() {
        return "sms";
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return CONFIG_PROPERTIES;
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        return INSTANCE;
    }

    @Override
    public void init(Config.Scope config) {
        // NOOP
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // NOOP
    }

    @Override
    public void close() {
        // NOOP
    }

    @Override
    public Map<String, String> getOperationalInfo() {
        return Collections.singletonMap("availableClients", SmsClientFactory.getAvailableClientNames().toString());
    }

}