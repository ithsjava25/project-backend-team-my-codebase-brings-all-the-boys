/* eslint-disable react-refresh/only-export-components */
// Context provider for authentication
import { createContext, useContext } from 'react';
import { useAuth } from '../hooks/useAuth';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    const auth = useAuth();
    return (
        <AuthContext.Provider value={auth}>
            {children}
        </AuthContext.Provider>
    );
}

export const useAuthContext = () => {
    const context = useContext(AuthContext);
    if (context === null) {
        throw new Error('useAuthContext must be used within an AuthProvider');
    }
    return context;
};