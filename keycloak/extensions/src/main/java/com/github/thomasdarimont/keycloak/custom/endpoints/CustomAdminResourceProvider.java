package com.github.thomasdarimont.keycloak.custom.endpoints;

import com.github.thomasdarimont.keycloak.custom.endpoints.admin.CustomAdminResource;
import com.github.thomasdarimont.keycloak.custom.endpoints.admin.UserProvisioningResource.UserProvisioningConfig;
import com.google.auto.service.AutoService;
import lombok.extern.jbosslog.JBossLog;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.services.resources.admin.AdminEventBuilder;
import org.keycloak.services.resources.admin.ext.AdminRealmResourceProvider;
import org.keycloak.services.resources.admin.ext.AdminRealmResourceProviderFactory;
import org.keycloak.services.resources.admin.permissions.AdminPermissionEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.logging.Level;
import java.util.regex.Pattern;

@JBossLog
public class CustomAdminResourceProvider implements AdminRealmResourceProvider {

    private static final Logger logger  = LoggerFactory.getLogger(CustomAdminResourceProvider.class);

    public static final String ID = "custom-admin-resources";

    private final UserProvisioningConfig privisioningConfig;

    public CustomAdminResourceProvider(UserProvisioningConfig privisioningConfig) {
        this.privisioningConfig = privisioningConfig;
    }

    @Override
    public Object getResource(KeycloakSession session, RealmModel realm, AdminPermissionEvaluator auth, AdminEventBuilder adminEvent) {
        return new CustomAdminResource(session, realm, auth, adminEvent, privisioningConfig);
    }

    @Override
    public void close() {

    }

    @AutoService(AdminRealmResourceProviderFactory.class)
    public static class Factory implements AdminRealmResourceProviderFactory {

        @Override
        public String getId() {
            return ID;
        }

        private CustomAdminResourceProvider customAdminResource;

        @Override
        public AdminRealmResourceProvider create(KeycloakSession session) {
            return customAdminResource;
        }

        @Override
        public void init(Config.Scope config) {
            logger.info("### Initialize custom admin resources with config: {} ,{}",
                    config.scope("users", "provisioning").get("required-realm-role"),
                    config.scope("users", "provisioning").get("managed-attribute-pattern")
            );
            // Retrieve realmRole with a default value if null
            String realmRole = config.scope("users", "provisioning").get("required-realm-role");
            if (realmRole == null) {
                logger.info("required-realm-role is not defined, using default value 'default-realm-role'");
                realmRole = "default-realm-role";
            }

            // Retrieve attributePatternString with a default value if null
            String attributePatternString = config.scope("users", "provisioning").get("managed-attribute-pattern");
            if (attributePatternString == null) {
                logger.info("managed-attribute-pattern is not defined, using default value '.*'");
                attributePatternString = ".*"; // You can use any default regex pattern that makes sense for your case
            }

            // Compile the pattern
            Pattern attributePattern = Pattern.compile(attributePatternString);

            // Create the provisioning config and initialize the custom admin resource
            var provisioningConfig = new UserProvisioningConfig(realmRole, attributePattern);
            customAdminResource = new CustomAdminResourceProvider(provisioningConfig);
        }

        @Override
        public void postInit(KeycloakSessionFactory factory) {
            log.info("### Register custom admin resources");
        }

        @Override
        public void close() {

        }
    }
}
