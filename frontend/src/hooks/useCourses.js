import { useState, useEffect } from 'react';
import { courseApi } from '../api/courses';

export function useCourses({page = 0, size = 10} = {}) {
  const [courses, setCourses] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchCourses = async () => {
      try {
        setLoading(true);
        const data = await courseApi.getUsersCourses({page, size});
        setCourses(data.content || []);
        setTotalPages(data.totalPages || 1);
      } catch (err) {
        setError(err.response?.data?.message || 'Failed to fetch courses');
      } finally {
        setLoading(false);
      }
    };

    fetchCourses().then(() => {});
  }, [page, size]);

  return { courses, totalPages, loading, error };
}
