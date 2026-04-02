import { useState, useEffect } from 'react';
import client from '../api/client';

export function useAuth() {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    const fetchUser = () => {
        setLoading(true);
        client.get('/auth/me')
            .then(res => setUser(res.data))
            .catch(() => setUser(null))
            .finally(() => setLoading(false));
    };

    useEffect(() => {
        fetchUser();
    }, []);

    const refetch = () => {
        fetchUser();
    };

    const logout = async () => {
        await client.post('/auth/logout');
        setUser(null);
    };

    return { user, loading, logout, refetch };
}