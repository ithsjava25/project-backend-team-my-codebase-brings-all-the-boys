// Custom hook for authentication
import { useState, useEffect } from 'react';
import client from '../api/client';

export function useAuth() {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        client.get('/auth/me')
            .then(res => setUser(res.data))
            .catch(() => setUser(null))  // 401 = ej inloggad
            .finally(() => setLoading(false));
    }, []);

    const logout = async () => {
        await client.post('/auth/logout');
        setUser(null);
    };

    return { user, loading, logout };
}