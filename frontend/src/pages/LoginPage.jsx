import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthContext } from '../context/AuthContext';

export default function LoginPage() {
    const { user } = useAuthContext();
    const navigate = useNavigate();

    useEffect(() => {
        if (user) navigate('/dashboard');
    }, [user]);

    return (
        <div>
            <h2>Login</h2>
            <a href="http://localhost:8080/oauth2/authorization/github">
                Login with GitHub
            </a>
        </div>
    );
}