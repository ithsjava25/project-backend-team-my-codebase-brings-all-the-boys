import {useState, useEffect} from 'react';
import {assignmentApi} from '../api/assignments';

export function useAssignmentDetail(id) {
    const [assignment, setAssignment] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        let cancelled = false;

        if (!id) {
            setAssignment(null);
            setError(null);
            setLoading(false);
            return;
        }

        const fetchDetail = async () => {
            try {
                setLoading(true);
                setError(null);
                const data = await assignmentApi.getAssignmentById(id);
                if (!cancelled) setAssignment(data);
            } catch (err) {
                if (!cancelled) {
                    setAssignment(null);
                    setError(err.response?.data?.message || 'Failed to fetch assignment details');
                }
            } finally {
                if (!cancelled) setLoading(false);
            }
        };

        void fetchDetail();
        return () => {
            cancelled = true
        };
    }, [id]);

    return {assignment, loading, error};
}