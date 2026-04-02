// Placeholder dashboard page

import { useAuthContext } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

export default function Dashboard() {
    const { user, logout } = useAuthContext();
    const navigate = useNavigate();

    const handleLogout = async () => {
        await logout();
        navigate('/login');
    };

    if (!user) {
        return <div>Loading...</div>;
    }

    return (
        <div style={{ padding: '2rem', maxWidth: '800px', margin: '0 auto' }}>
            <h1>Dashboard</h1>

            <div style={{
                padding: '1.5rem',
                border: '1px solid #ddd',
                borderRadius: '8px',
                marginBottom: '1.5rem'
            }}>
                <h2>Välkommen, {user.username}!</h2>
                <p><strong>Email:</strong> {user.email || 'Ej angiven'}</p>
                <p><strong>Role:</strong> {user.role?.name || 'Ej angiven'}</p>
                <p><strong>User ID:</strong> {user.id}</p>
            </div>

            <button
                onClick={handleLogout}
                style={{
                    padding: '0.75rem 1.5rem',
                    backgroundColor: '#e74c3c',
                    color: 'white',
                    border: 'none',
                    borderRadius: '4px',
                    cursor: 'pointer',
                    fontSize: '1rem'
                }}
            >
                Logga ut
            </button>

            <div style={{ marginTop: '2rem', padding: '1rem', backgroundColor: '#f8f9fa', borderRadius: '4px' }}>
                <h3>Session Status</h3>
                <p>✅ Inloggad via session cookie</p>
                <p>🔒 CSRF-skydd aktivt (via OAuth2 state parameter)</p>
                <p>🍪 Kolla DevTools → Application → Cookies för att se session-cookien</p>
            </div>
        </div>
    );
}