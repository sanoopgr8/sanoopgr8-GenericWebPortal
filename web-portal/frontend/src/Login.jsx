import PropTypes from 'prop-types';
import { useState, useEffect } from 'react';
import { useKeycloak } from './KeycloakProvider';
import logo from './assets/logo.png';

function Login({ onLogin }) {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState('');
    const [messageType, setMessageType] = useState('');
    const [showLocalLogin, setShowLocalLogin] = useState(false);
    const { ssoEnabled, login: ssoLogin, loading } = useKeycloak();

    const handleLocalSubmit = async (e) => {
        e.preventDefault();
        setMessage('');
        try {
            const response = await fetch('/api/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password }),
            });

            if (!response.ok) {
                // Try to parse error response
                try {
                    const errorData = await response.json();
                    setMessageType('error');
                    setMessage(errorData.message || `Login failed: ${response.status} ${response.statusText}`);
                } catch {
                    setMessageType('error');
                    setMessage(`Login failed: ${response.status} ${response.statusText}`);
                }
                return;
            }

            const data = await response.json();

            if (data.status === 'success') {
                setMessageType('success');
                setMessage(data.message);
                onLogin(`${data.firstName} ${data.lastName}`);
            } else {
                setMessageType('error');
                setMessage(data.message || 'Login failed');
            }
        } catch (error) {
            console.error('Error:', error);
            setMessageType('error');
            setMessage('Login failed: ' + error.message);
        }
    };

    const handleSsoLogin = () => {
        ssoLogin();
    };

    if (loading) {
        return (
            <div className="card">
                <img src={logo} alt="Logo" style={{ width: '100px', marginBottom: '20px' }} />
                <h2>Loading...</h2>
            </div>
        );
    }

    return (
        <div className="card">
            <img src={logo} alt="Logo" style={{ width: '100px', marginBottom: '20px' }} />
            <h2>Login</h2>

            {/* SSO Login Button (Primary) */}
            {ssoEnabled && (
                <>
                    <button
                        onClick={handleSsoLogin}
                        className="sso-button"
                        style={{
                            width: '100%',
                            padding: '12px',
                            marginBottom: '20px',
                            background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                            color: 'white',
                            border: 'none',
                            borderRadius: '8px',
                            fontSize: '16px',
                            fontWeight: '600',
                            cursor: 'pointer',
                            transition: 'transform 0.2s, box-shadow 0.2s',
                        }}
                        onMouseEnter={(e) => {
                            e.target.style.transform = 'translateY(-2px)';
                            e.target.style.boxShadow = '0 8px 16px rgba(102, 126, 234, 0.4)';
                        }}
                        onMouseLeave={(e) => {
                            e.target.style.transform = 'translateY(0)';
                            e.target.style.boxShadow = 'none';
                        }}
                    >
                        🔐 Sign in with SSO
                    </button>

                    {/* Divider */}
                    <div style={{
                        display: 'flex',
                        alignItems: 'center',
                        margin: '20px 0',
                        color: 'rgba(255, 255, 255, 0.5)',
                    }}>
                        <div style={{ flex: 1, height: '1px', background: 'rgba(255, 255, 255, 0.2)' }}></div>
                        <span
                            style={{
                                padding: '0 15px',
                                fontSize: '14px',
                                cursor: 'pointer',
                                userSelect: 'none',
                            }}
                            onClick={() => setShowLocalLogin(!showLocalLogin)}
                        >
                            {showLocalLogin ? '▼' : '▶'} Or use local account
                        </span>
                        <div style={{ flex: 1, height: '1px', background: 'rgba(255, 255, 255, 0.2)' }}></div>
                    </div>
                </>
            )}

            {/* Local Login Form (Backup) */}
            {(!ssoEnabled || showLocalLogin) && (
                <form onSubmit={handleLocalSubmit}>
                    <div className="form-group">
                        <input
                            type="email"
                            placeholder="Email Address"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <input
                            type="password"
                            placeholder="Password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>
                    <button type="submit">Login with Email</button>
                </form>
            )}

            {message && <p className={`message ${messageType}`}>{message}</p>}
        </div>
    );
}

Login.propTypes = {
    onLogin: PropTypes.func.isRequired
};

export default Login;
