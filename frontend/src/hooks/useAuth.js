import { useState, useEffect, useCallback } from 'react';
import client from '../api/client';

export function useAuth() {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    const fetchUser = useCallback(() => {
        setLoading(true);
        return client.get('/auth/me')
            .then(res => setUser(res.data))
            .catch((error) => {
                if (error.response?.status === 401 || error.response?.status === 403) {
                    setUser(null);
                    return;
                }
                throw error;
            })
            .finally(() => setLoading(false));
    }, []);

    useEffect(() => {
        // eslint-disable-next-line react-hooks/set-state-in-effect
        fetchUser();
    }, [fetchUser]);

    const refetch = () => {
        return fetchUser();
    };

    const logout = async () => {
        await client.post('/auth/logout');
        setUser(null);
    };

    return { user, loading, logout, refetch };
}