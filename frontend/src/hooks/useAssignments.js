import { useState, useEffect } from 'react';
import { assignmentApi } from '../api/assignments';

export function useAssignments() {
  const [assignments, setAssignments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchAssignments = async () => {
      try {
        setLoading(true);
        const data = await assignmentApi.getAllAssignments();
        setAssignments(data);
      } catch (err) {
        setError(err.response?.data?.message || 'Failed to fetch assignments');
      } finally {
        setLoading(false);
      }
    };

    void fetchAssignments();
  }, []);

  return { assignments, loading, error };
}