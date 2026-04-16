import { useState, useEffect } from 'react';
import { assignmentApi } from '../api/assignments';

export function useAssignmentDetail(id) {
  const [assignment, setAssignment] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!id) return;

    const fetchDetail = async () => {
      try {
        setLoading(true);
        const data = await assignmentApi.getAssignmentById(id);
        setAssignment(data);
      } catch (err) {
        setError(err.response?.data?.message || 'Failed to fetch assignment details');
      } finally {
        setLoading(false);
      }
    };

    void fetchDetail();
  }, [id]);

  return { assignment, loading, error };
}