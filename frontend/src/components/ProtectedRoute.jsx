// Wrapper component for routes that require authentication
import { Navigate } from 'react-router-dom';
import { useAuthContext } from '../context/AuthContext';

export function ProtectedRoute({ children }) {
    const { user, loading } = useAuthContext();

    if (loading) return <div role="status" aria-live="polite">Loading...</div>;
    if (!user) return <Navigate to="/login" replace />;

    return children;
}