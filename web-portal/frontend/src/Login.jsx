import { useState } from 'react';
import logo from './assets/logo.png';

function Login({ onLogin }) {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState('');
    const [messageType, setMessageType] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await fetch('/api/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password }),
            });
            const data = await response.json();

            if (data.status === 'success') {
                setMessageType('success');
                setMessage(data.message);
                onLogin(`${data.firstName} ${data.lastName}`);
            } else {
                setMessageType('error');
                setMessage(data.message);
            }
        } catch (error) {
            console.error('Error:', error);
            setMessageType('error');
            setMessage('Login failed');
        }
    };

    return (
        <div className="card">
            <img src={logo} alt="Logo" style={{ width: '100px', marginBottom: '20px' }} />
            <h2>Login</h2>
            <form onSubmit={handleSubmit}>
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
                <button type="submit">Login</button>
            </form>
            {message && <p className={`message ${messageType}`}>{message}</p>}
        </div>
    );
}

export default Login;
