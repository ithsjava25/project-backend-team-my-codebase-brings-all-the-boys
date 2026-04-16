import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthContext } from '../context/AuthContext';
import client from '../api/client';
import { LoginForm } from '@/components/login-form';

export default function LoginPage() {
    const { user, refetch } = useAuthContext();
    const [error, setError] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const navigate = useNavigate();
    const githubOAuthUrl = `${import.meta.env.VITE_API_BASE_URL}/oauth2/authorization/github`;

    useEffect(() => {
        if (user) {
            navigate('/dashboard');
        }
    }, [user, navigate]);

    const handleSubmit = async (formData, mode) => {
        setError('');
        setIsLoading(true);

        try {
            if (mode === 'login') {
                await client.post('/auth/login', {
                    username: formData.username,
                    password: formData.password
                });
            } else {
                await client.post('/auth/register', {
                    username: formData.username,
                    email: formData.email,
                    password: formData.password,
                    confirmPassword: formData.confirmPassword
                });
            }
            
            await refetch();
            navigate('/dashboard');
        } catch (err) {
            const errorMsg = err.response?.data?.error
                || err.response?.data?.message
                || err.response?.data
                || 'Unexpected error. Please try again.';
            setError(typeof errorMsg === 'string' ? errorMsg : JSON.stringify(errorMsg));
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-muted p-4">
            <div className="w-full max-w-md">
                <LoginForm 
                    onSubmit={handleSubmit}
                    error={error}
                    isLoading={isLoading}
                    githubOAuthUrl={githubOAuthUrl}
                />
            </div>
        </div>
    );
}
