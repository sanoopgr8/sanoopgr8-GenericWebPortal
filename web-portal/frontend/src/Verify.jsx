import { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import logo from './assets/logo.png';

function Verify() {
    const [searchParams] = useSearchParams();
    const [message, setMessage] = useState('Verifying your email...');
    const [messageType, setMessageType] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        const token = searchParams.get('token');

        if (!token) {
            setMessageType('error');
            setMessage('Invalid verification link');
            return;
        }

        const verifyEmail = async () => {
            try {
                const response = await fetch(`/api/verify?token=${token}`);
                const data = await response.json();

                if (data.status === 'success') {
                    setMessageType('success');
                    setMessage(data.message);
                    // Redirect to login after 3 seconds
                    setTimeout(() => navigate('/'), 3000);
                } else {
                    setMessageType('error');
                    setMessage(data.message);
                }
            } catch (error) {
                console.error('Error:', error);
                setMessageType('error');
                setMessage('Verification failed. Please try again.');
            }
        };

        verifyEmail();
    }, [searchParams, navigate]);

    return (
        <div className="container">
            <h1>Web Portal</h1>
            <div className="card">
                <img src={logo} alt="Logo" style={{ width: '100px', marginBottom: '20px' }} />
                <h2>Email Verification</h2>
                <p className={`message ${messageType}`}>{message}</p>
                {messageType === 'success' && (
                    <p>Redirecting to login...</p>
                )}
            </div>
        </div>
    );
}

export default Verify;
