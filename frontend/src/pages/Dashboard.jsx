// Placeholder dashboard page

import { useAuthContext } from '../context/AuthContext';
import {Navigate, useNavigate} from 'react-router-dom';

export default function Dashboard() {
    const { user, logout, loading } = useAuthContext();
    const navigate = useNavigate();

    const handleLogout = async () => {
      try {
        await logout();
      } catch (err) {
        console.error('Logout failed:', err);
      } finally {
        navigate('/login');
      }
    };

    return (
        <div style={{ padding: '2rem', maxWidth: '800px', margin: '0 auto' }}>
            <h1>Dashboard</h1>

            <div style={{
                padding: '1.5rem',
                border: '1px solid #ddd',
                borderRadius: '8px',
                marginBottom: '1.5rem'
            }}>
                <h2>Welcome, {user.username}!</h2>
                <p><strong>Email:</strong> {user.email || 'Not specified'}</p>
                <p><strong>Role:</strong> {user.role?.name || 'Not specified'}</p>
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
                Logout
            </button>

            <div style={{ marginTop: '2rem', padding: '1rem', backgroundColor: 'rgba(51,54,60,0.46)', borderRadius: '4px' }}>
                <h2>Placeholder Page</h2>
                <h3>Wait.. Here already? Quick.. Put some placeholders in!</h3>
                <p>✅ Logged in via session cookie</p>
                <p>Check DevTools → Application → Cookies to see the session cookie</p>
            </div>
        </div>
    );
}