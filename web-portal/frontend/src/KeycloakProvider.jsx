import { createContext, useState, useEffect, useContext } from 'react';
import PropTypes from 'prop-types';
import { initKeycloak } from './keycloak';

const KeycloakContext = createContext(null);

export const useKeycloak = () => {
    const context = useContext(KeycloakContext);
    if (!context) {
        throw new Error('useKeycloak must be used within KeycloakProvider');
    }
    return context;
};

export const KeycloakProvider = ({ children }) => {
    const [keycloak, setKeycloak] = useState(null);
    const [authenticated, setAuthenticated] = useState(false);
    const [loading, setLoading] = useState(true);
    const [user, setUser] = useState(null);
    const [ssoEnabled, setSsoEnabled] = useState(false);

    useEffect(() => {
        const initialize = async () => {
            try {
                const kc = await initKeycloak();

                if (!kc) {
                    // SSO is disabled or not configured
                    console.log('Keycloak SSO is not enabled or configured');
                    setSsoEnabled(false);
                    setLoading(false);
                    return;
                }

                setSsoEnabled(true);
                setKeycloak(kc);

                // Initialize Keycloak
                const auth = await kc.init({
                    onLoad: 'check-sso',
                    silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
                    pkceMethod: 'S256',
                    checkLoginIframe: false, // Disable iframe check for dev mode
                });

                setAuthenticated(auth);

                if (auth) {
                    // Get user info
                    try {
                        const userInfo = await kc.loadUserProfile();
                        setUser({
                            id: kc.subject,
                            email: userInfo.email,
                            firstName: userInfo.firstName,
                            lastName: userInfo.lastName,
                            fullName: `${userInfo.firstName} ${userInfo.lastName}`,
                        });

                        // Set up token refresh
                        setInterval(() => {
                            kc.updateToken(30).catch(() => {
                                console.error('Failed to refresh token');
                                setAuthenticated(false);
                                setUser(null);
                            });
                        }, 30000); // Check every 30 seconds
                    } catch (profileError) {
                        console.error('Failed to load user profile:', profileError);
                        // Continue anyway, user might not have profile set up
                    }
                }

                setLoading(false);
            } catch (error) {
                console.error('Keycloak initialization failed:', error);
                // Don't block the app, just disable SSO
                setSsoEnabled(false);
                setLoading(false);
            }
        };

        initialize();
    }, []);

    const login = () => {
        if (keycloak) {
            keycloak.login({
                redirectUri: window.location.origin,
            });
        }
    };

    const logout = () => {
        if (keycloak) {
            keycloak.logout({
                redirectUri: window.location.origin,
            });
        }
    };

    const getToken = () => {
        return keycloak?.token;
    };

    const value = {
        keycloak,
        authenticated,
        loading,
        user,
        ssoEnabled,
        login,
        logout,
        getToken,
    };

    return (
        <KeycloakContext.Provider value={value}>
            {children}
        </KeycloakContext.Provider>
    );
};

KeycloakProvider.propTypes = {
    children: PropTypes.node.isRequired,
};
