import {useState, useEffect, useCallback} from 'react';
import {courseApi} from '../api/courses';

export function useCourses({page = 0, size = 10} = {}) {
    const [courses, setCourses] = useState([]);
    const [totalPages, setTotalPages] = useState(0);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [refreshTrigger, setRefreshTrigger] = useState(0);

    const refresh = useCallback(() => setRefreshTrigger(prev => prev + 1), []);

    useEffect(() => {
        window.addEventListener('courses-changed', refresh);
        return () => window.removeEventListener('courses-changed', refresh);
    }, [refresh]);

    useEffect(() => {
        let cancelled = false;
        const fetchCourses = async () => {
            try {
                setLoading(true);
                setError(null);
                const data = await courseApi.getUsersCourses({page, size});
                if (!cancelled) {
                    setCourses(data.content || []);
                    setTotalPages(data.totalPages || 1);
                    setError(null);
                }
            } catch (err) {
                if (!cancelled) {
                    setError(err.response?.data?.message || 'Failed to fetch courses');
                }
            } finally {
                if (!cancelled) {
                    setLoading(false);
                }
            }
        };

        fetchCourses();
        return () => {
            cancelled = true;
        };
    }, [page, size, refreshTrigger]);

    return {courses, totalPages, loading, error, refresh};
}
