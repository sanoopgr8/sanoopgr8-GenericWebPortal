import Keycloak from 'keycloak-js';

// This will be initialized dynamically from backend configuration
let keycloakInstance = null;

export const initKeycloak = async () => {
    try {
        // Fetch Keycloak configuration from backend
        const response = await fetch('/api/auth/sso/config');

        if (!response.ok) {
            // Backend doesn't have Keycloak endpoints yet or SSO is not configured
            console.log('Keycloak configuration endpoint not available. SSO disabled.');
            return null;
        }

        const config = await response.json();

        if (!config.enabled) {
            console.log('Keycloak SSO is disabled');
            return null;
        }

        // Initialize Keycloak with configuration from backend
        keycloakInstance = new Keycloak({
            url: config.serverUrl,
            realm: config.realm,
            clientId: config.clientId,
        });

        return keycloakInstance;
    } catch (error) {
        console.error('Failed to initialize Keycloak:', error);
        // Return null to disable SSO gracefully
        return null;
    }
};

export const getKeycloak = () => keycloakInstance;

export default keycloakInstance;
