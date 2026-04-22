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
                const status = error.response?.status;
                // 401/403 means not logged in
                // 502/503/504 often means backend is still starting up in dev
                if (status === 401 || status === 403 || status === 502 || status === 503 || status === 504) {
                    setUser(null);
                    if (status >= 500) {
                        console.warn("Backend might be starting up or temporarily unavailable...");
                    }
                    return;
                }
                console.error("Authentication check failed:", error);
                setUser(null);
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