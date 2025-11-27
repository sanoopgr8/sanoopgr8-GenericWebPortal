import PropTypes from 'prop-types'
import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import './App.css'

function Settings({ user }) {
    const navigate = useNavigate()
    const [config, setConfig] = useState({
        host: '',
        port: 587,
        username: '',
        password: '',
        protocol: 'smtp',
        auth: true,
        starttls: true,
        fromEmail: '',
        fromName: ''
    })
    const [keycloakConfig, setKeycloakConfig] = useState({
        enabled: true,
        serverUrl: '',
        realm: '',
        clientId: '',
        clientSecret: ''
    })
    const [message, setMessage] = useState('')
    const [error, setError] = useState('')
    const [keycloakMessage, setKeycloakMessage] = useState('')
    const [keycloakError, setKeycloakError] = useState('')
    const [testing, setTesting] = useState(false)

    useEffect(() => {
        if (!user) {
            navigate('/')
            return
        }

        // Fetch mail config
        fetch('/api/settings/mail')
            .then(res => {
                if (!res.ok) throw new Error('Failed to fetch settings')
                return res.json()
            })
            .then(data => setConfig(data))
            .catch(err => setError('Could not load mail settings'))

        // Fetch Keycloak config
        fetch('/api/settings/keycloak')
            .then(res => {
                if (!res.ok) throw new Error('Failed to fetch Keycloak settings')
                return res.json()
            })
            .then(data => setKeycloakConfig(data))
            .catch(err => setKeycloakError('Could not load Keycloak settings'))
    }, [user, navigate])

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target
        setConfig(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }))
    }

    const handleKeycloakChange = (e) => {
        const { name, value, type, checked } = e.target
        setKeycloakConfig(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }))
    }

    const handleSubmit = async (e) => {
        e.preventDefault()
        setMessage('')
        setError('')

        try {
            const res = await fetch('/api/settings/mail', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(config)
            })

            if (res.ok) {
                setMessage('Settings saved successfully!')
            } else {
                setError('Failed to save settings')
            }
        } catch (err) {
            setError('Error saving settings')
        }
    }

    const handleKeycloakSubmit = async (e) => {
        e.preventDefault()
        setKeycloakMessage('')
        setKeycloakError('')

        try {
            const res = await fetch('/api/settings/keycloak', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(keycloakConfig)
            })

            const data = await res.json()

            if (res.ok && data.status === 'success') {
                setKeycloakMessage('Keycloak settings saved successfully! Please refresh the page for changes to take effect.')
            } else {
                setKeycloakError(data.message || 'Failed to save Keycloak settings')
            }
        } catch (err) {
            setKeycloakError('Error saving Keycloak settings')
        }
    }

    const handleTestConnection = async () => {
        setTesting(true)
        setKeycloakMessage('')
        setKeycloakError('')

        try {
            const res = await fetch('/api/settings/keycloak/test', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(keycloakConfig)
            })

            const data = await res.json()

            if (data.success) {
                setKeycloakMessage(`✓ Connection successful! Issuer: ${data.issuer}`)
            } else {
                setKeycloakError(`✗ Connection failed: ${data.message}`)
            }
        } catch (err) {
            setKeycloakError('Error testing connection')
        } finally {
            setTesting(false)
        }
    }

    return (
        <div className="app-layout">
            <nav className="sidebar">
                <div className="sidebar-header">
                    <div className="logo-icon">WP</div>
                </div>
                <div className="nav-items">
                    <button
                        className="nav-item"
                        title="Home"
                        onClick={() => navigate('/')}
                    >
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                            <path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
                            <polyline points="9 22 9 12 15 12 15 22"></polyline>
                        </svg>
                        <span className="nav-label">Home</span>
                    </button>
                    <button
                        className="nav-item active"
                        title="Settings"
                        onClick={() => navigate('/settings')}
                    >
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                            <circle cx="12" cy="12" r="3"></circle>
                            <path d="M12 1v6m0 6v6m-6-6h6m6 0h-6M4.93 4.93l4.24 4.24m5.66 5.66l4.24 4.24M4.93 19.07l4.24-4.24m5.66-5.66l4.24-4.24"></path>
                        </svg>
                        <span className="nav-label">Settings</span>
                    </button>
                </div>
                <div className="sidebar-footer">
                    <button
                        className="nav-item logout"
                        title="Logout"
                        onClick={() => navigate('/')}
                    >
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
                            <polyline points="16 17 21 12 16 7"></polyline>
                            <line x1="21" y1="12" x2="9" y2="12"></line>
                        </svg>
                        <span className="nav-label">Logout</span>
                    </button>
                </div>
            </nav>
            <main className="main-content">
                <div className="container">
                    {/* Mail Server Settings */}
                    <div className="card">
                        <h2>Mail Server Settings</h2>

                        {message && <div className="success-message">{message}</div>}
                        {error && <div className="error-message-box">{error}</div>}

                        <form onSubmit={handleSubmit}>
                            <div className="form-group">
                                <label>SMTP Host</label>
                                <input
                                    type="text"
                                    name="host"
                                    value={config.host}
                                    onChange={handleChange}
                                    required
                                />
                            </div>

                            <div className="form-group">
                                <label>SMTP Port</label>
                                <input
                                    type="number"
                                    name="port"
                                    value={config.port}
                                    onChange={handleChange}
                                    required
                                />
                            </div>

                            <div className="form-group">
                                <label>Username</label>
                                <input
                                    type="text"
                                    name="username"
                                    value={config.username}
                                    onChange={handleChange}
                                    required
                                />
                            </div>

                            <div className="form-group">
                                <label>Password</label>
                                <input
                                    type="password"
                                    name="password"
                                    value={config.password}
                                    onChange={handleChange}
                                    required
                                />
                            </div>

                            <div className="form-group">
                                <label>From Email</label>
                                <input
                                    type="email"
                                    name="fromEmail"
                                    value={config.fromEmail}
                                    onChange={handleChange}
                                    required
                                />
                            </div>

                            <div className="form-group">
                                <label>From Name</label>
                                <input
                                    type="text"
                                    name="fromName"
                                    value={config.fromName}
                                    onChange={handleChange}
                                    required
                                />
                            </div>

                            <div className="checkbox-group">
                                <label>
                                    <input
                                        type="checkbox"
                                        name="auth"
                                        checked={config.auth}
                                        onChange={handleChange}
                                    />
                                    SMTP Auth
                                </label>

                                <label>
                                    <input
                                        type="checkbox"
                                        name="starttls"
                                        checked={config.starttls}
                                        onChange={handleChange}
                                    />
                                    STARTTLS
                                </label>
                            </div>

                            <button type="submit">Save Settings</button>
                        </form>
                    </div>

                    {/* Keycloak SSO Configuration */}
                    <div className="card" style={{ marginTop: '30px' }}>
                        <h2>🔐 SSO Configuration (Keycloak)</h2>
                        <p style={{ color: 'rgba(255, 255, 255, 0.7)', fontSize: '14px', marginBottom: '20px' }}>
                            Configure Keycloak for Single Sign-On with Active Directory
                        </p>

                        {keycloakMessage && <div className="success-message">{keycloakMessage}</div>}
                        {keycloakError && <div className="error-message-box">{keycloakError}</div>}

                        <form onSubmit={handleKeycloakSubmit}>
                            <div className="checkbox-group" style={{ marginBottom: '20px' }}>
                                <label>
                                    <input
                                        type="checkbox"
                                        name="enabled"
                                        checked={keycloakConfig.enabled}
                                        onChange={handleKeycloakChange}
                                    />
                                    Enable SSO
                                </label>
                            </div>

                            <div className="form-group">
                                <label>Keycloak Server URL</label>
                                <input
                                    type="text"
                                    name="serverUrl"
                                    value={keycloakConfig.serverUrl}
                                    onChange={handleKeycloakChange}
                                    placeholder="http://localhost:8180"
                                    required
                                />
                                <small style={{ color: 'rgba(255, 255, 255, 0.5)', fontSize: '12px' }}>
                                    Example: http://localhost:8180
                                </small>
                            </div>

                            <div className="form-group">
                                <label>Realm</label>
                                <input
                                    type="text"
                                    name="realm"
                                    value={keycloakConfig.realm}
                                    onChange={handleKeycloakChange}
                                    placeholder="webportal"
                                    required
                                />
                            </div>

                            <div className="form-group">
                                <label>Client ID</label>
                                <input
                                    type="text"
                                    name="clientId"
                                    value={keycloakConfig.clientId}
                                    onChange={handleKeycloakChange}
                                    placeholder="webportal-client"
                                    required
                                />
                            </div>

                            <div className="form-group">
                                <label>Client Secret</label>
                                <input
                                    type="password"
                                    name="clientSecret"
                                    value={keycloakConfig.clientSecret}
                                    onChange={handleKeycloakChange}
                                    placeholder="Enter client secret from Keycloak"
                                />
                                <small style={{ color: 'rgba(255, 255, 255, 0.5)', fontSize: '12px' }}>
                                    Get this from Keycloak Admin Console → Clients → webportal-client → Credentials
                                </small>
                            </div>

                            <div style={{ display: 'flex', gap: '10px' }}>
                                <button type="submit" style={{ flex: 1 }}>Save Configuration</button>
                                <button
                                    type="button"
                                    onClick={handleTestConnection}
                                    disabled={testing}
                                    style={{
                                        flex: 1,
                                        background: testing ? '#666' : 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                                        cursor: testing ? 'not-allowed' : 'pointer'
                                    }}
                                >
                                    {testing ? 'Testing...' : 'Test Connection'}
                                </button>
                            </div>
                        </form>

                        <div style={{
                            marginTop: '20px',
                            padding: '15px',
                            background: 'rgba(102, 126, 234, 0.1)',
                            borderRadius: '8px',
                            border: '1px solid rgba(102, 126, 234, 0.3)'
                        }}>
                            <h4 style={{ margin: '0 0 10px 0', color: '#667eea' }}>📖 Setup Guide</h4>
                            <p style={{ margin: '5px 0', fontSize: '13px', color: 'rgba(255, 255, 255, 0.7)' }}>
                                1. Install Keycloak: Run <code>.\scripts\install-keycloak.ps1</code>
                            </p>
                            <p style={{ margin: '5px 0', fontSize: '13px', color: 'rgba(255, 255, 255, 0.7)' }}>
                                2. Start Keycloak: <code>cd C:\keycloak && .\bin\kc.bat start-dev --http-port=8180</code>
                            </p>
                            <p style={{ margin: '5px 0', fontSize: '13px', color: 'rgba(255, 255, 255, 0.7)' }}>
                                3. Access admin console: <a href="http://localhost:8180" target="_blank" rel="noopener noreferrer" style={{ color: '#667eea' }}>http://localhost:8180</a>
                            </p>
                            <p style={{ margin: '5px 0', fontSize: '13px', color: 'rgba(255, 255, 255, 0.7)' }}>
                                4. Follow the setup guide in <code>docs/KEYCLOAK_SETUP.md</code>
                            </p>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    )
}

Settings.propTypes = {
    user: PropTypes.string
}

export default Settings
