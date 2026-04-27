import {useState, useEffect} from 'react';
import {assignmentApi} from '../api/assignments';

export function useAssignments({page = 0, size = 10} = {}) {
    const [assignments, setAssignments] = useState([]);
    const [totalPages, setTotalPages] = useState(0);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const controller = new AbortController();
        const fetchAssignments = async () => {
            try {
                setLoading(true);
                setError(null);
                const data = await assignmentApi.getAllAssignments({page, size}, controller.signal);
                if (!controller.signal.aborted) {
                    // Spring Data Page object has the list in 'content'
                    setAssignments(data.content || []);
                    setTotalPages(data.totalPages ?? 0);
                }
            } catch (err) {
                if (err.name !== 'AbortError' && err.name !== 'CanceledError') {
                    setError(err.response?.data?.message || 'Failed to fetch assignments');
                }
            } finally {
                if (!controller.signal.aborted) {
                    setLoading(false);
                }
            }
        };

        fetchAssignments();
        return () => {
            controller.abort();
        };
    }, [page, size]);

    return {assignments, totalPages, loading, error};
}