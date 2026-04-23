import { Navigate } from 'react-router-dom';
import { useAuthContext } from '../context/AuthContext';

export function ProtectedRoute({ children, requiredRole, allowedRoles }) {
    const { user, loading } = useAuthContext();

    if (loading) return <div role="status" aria-live="polite">Loading...</div>;
    if (!user) return <Navigate to="/login" replace />;

    // Check specific role
    if (requiredRole && user?.role?.name !== requiredRole) {
        return <Navigate to="/dashboard" replace />;
    }

    // Check if user's role is in allowed roles list
    if (allowedRoles && !allowedRoles.includes(user?.role?.name)) {
        return <Navigate to="/dashboard" replace />;
    }

    return children;
}