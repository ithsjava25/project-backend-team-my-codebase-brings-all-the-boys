import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthContext } from '../context/AuthContext';
import client from '../api/client';

export default function LoginPage() {
    const { user, refetch } = useAuthContext();
    const [error, setError] = useState('');
    const navigate = useNavigate();
    const [isLogin, setIsLogin] = useState(true);
    const [formData, setFormData] = useState({
        username: '',
        email: '',
        password: '',
        confirmPassword: ''
    });

    useEffect(() => {
        if (user) {
            navigate('/dashboard');
        }
    }, [user, navigate]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        try {
            if (isLogin) {
                // Login
                await client.post('/auth/login', {
                    username: formData.username,
                    password: formData.password
                });
            } else {
                // Register
                await client.post('/auth/register', {
                    username: formData.username,
                    email: formData.email,
                    password: formData.password,
                    confirmPassword: formData.confirmPassword
                });
            }
            // AuthContext gets user via /me
            await refetch();
            navigate('/dashboard');
        } catch (err) {
            const errorMsg = err.response?.data?.error
                || err.response?.data?.message
                || err.response?.data
                || 'Unexpected error. Please try again.';
            setError(typeof errorMsg === 'string' ? errorMsg : JSON.stringify(errorMsg));
        }
    };

    return (
        <div style={{ minHeight: '100vh', display: 'flex', justifyContent: 'center', alignItems: 'center', background: 'black' }}>
            <div style={{ backgroundColor: '#24292e', padding: '2rem', borderRadius: '8px', width: '100%', maxWidth: '400px' }}>
                <h1 style={{ textAlign: 'center', marginBottom: '1.5rem' }}>
                    {isLogin ? 'Login' : 'Register'}
                </h1>

                {/* Toggle */}
                <div style={{ display: 'flex', marginBottom: '1.5rem', gap: '1rem' }}>
                    <button
                        onClick={() => setIsLogin(true)}
                        style={{
                            flex: 1,
                            padding: '0.5rem',
                            background: isLogin ? '#4299e1' : '#e2e8f0',
                            color: isLogin ? 'white' : 'black',
                            border: 'none',
                            borderRadius: '4px',
                            cursor: 'pointer'
                        }}
                    >
                        Login
                    </button>
                    <button
                        onClick={() => setIsLogin(false)}
                        style={{
                            flex: 1,
                            padding: '0.5rem',
                            background: !isLogin ? '#4299e1' : '#e2e8f0',
                            color: !isLogin ? 'white' : 'black',
                            border: 'none',
                            borderRadius: '4px',
                            cursor: 'pointer'
                        }}
                    >
                        Register
                    </button>
                </div>

                {/* Error */}
                {error && (
                    <div style={{
                        padding: '1rem',
                        background: '#fed7d7',
                        color: '#c53030',
                        borderRadius: '4px',
                        marginBottom: '1rem',
                        fontSize: '0.95rem',
                        fontWeight: '500'
                    }}>
                        ⚠️ {error}
                    </div>
                )}

                {/* Form */}
                <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                    <input
                        type="text"
                        placeholder="Username"
                        value={formData.username}
                        onChange={(e) => setFormData({...formData, username: e.target.value})}
                        required
                        style={{ padding: '0.75rem', border: '1px solid #cbd5e0', borderRadius: '4px' }}
                    />

                    {!isLogin && (
                        <input
                            type="email"
                            placeholder="Email"
                            value={formData.email}
                            onChange={(e) => setFormData({...formData, email: e.target.value})}
                            required
                            style={{ padding: '0.75rem', border: '1px solid #cbd5e0', borderRadius: '4px' }}
                        />
                    )}

                    <input
                        type="password"
                        placeholder="Password"
                        value={formData.password}
                        onChange={(e) => setFormData({...formData, password: e.target.value})}
                        required
                        style={{ padding: '0.75rem', border: '1px solid #cbd5e0', borderRadius: '4px' }}
                    />

                    {!isLogin && (
                        <input
                            type="password"
                            placeholder="Confirm Password"
                            value={formData.confirmPassword}
                            onChange={(e) => setFormData({...formData, confirmPassword: e.target.value})}
                            required
                            style={{ padding: '0.75rem', border: '1px solid #cbd5e0', borderRadius: '4px' }}
                        />
                    )}

                    <button
                        type="submit"
                        style={{
                            padding: '0.75rem',
                            background: '#4299e1',
                            color: 'white',
                            border: 'none',
                            borderRadius: '4px',
                            cursor: 'pointer',
                            fontSize: '1rem',
                            fontWeight: 'bold'
                        }}
                    >
                        {isLogin ? 'Login' : 'Register'}
                    </button>
                </form>

                {/* Divider */}
                <div style={{ display: 'flex', alignItems: 'center', margin: '1.5rem 0' }}>
                    <div style={{ flex: 1, height: '1px', background: '#e2e8f0' }} />
                    <span style={{ padding: '0 1rem', color: '#718096' }}>OR</span>
                    <div style={{ flex: 1, height: '1px', background: '#e2e8f0' }} />
                </div>

                {/* GitHub OAuth */}
                <a
                    href="http://localhost:8080/oauth2/authorization/github"
                    style={{
                        display: 'block',
                        textAlign: 'center',
                        padding: '0.75rem',
                        background: '#1e2124',
                        color: 'white',
                        textDecoration: 'none',
                        borderRadius: '4px',
                        fontWeight: 'bold'
                    }}
                >
                    Login using GitHub
                </a>
            </div>
        </div>
    );
}