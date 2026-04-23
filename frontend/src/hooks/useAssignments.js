import { useState, useEffect } from 'react';
import { assignmentApi } from '../api/assignments';

export function useAssignments({page = 0, size = 10} = {}) {
  const [assignments, setAssignments] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let cancelled = false;
    const fetchAssignments = async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await assignmentApi.getAllAssignments({ page, size });
        if (!cancelled) {
          // Spring Data Page object has the list in 'content'
          setAssignments(data.content || []);
          setTotalPages(data.totalPages || 1);
        }
      } catch (err) {
        if (!cancelled) {
          setError(err.response?.data?.message || 'Failed to fetch assignments');
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    };

    fetchAssignments();
    return () => {
      cancelled = true;
    };
  }, [page, size]);

  return { assignments, totalPages, loading, error };
}