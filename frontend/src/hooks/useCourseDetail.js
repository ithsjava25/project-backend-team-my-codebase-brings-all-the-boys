import { useState, useEffect } from 'react';
import { courseApi } from '../api/courses';

export function useCourseDetail(courseId) {
  const [course, setCourse] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!courseId) {
      setLoading(false);
      return;
    }

    const fetchCourse = async () => {
      try {
        setLoading(true);
        const data = await courseApi.getCourseById(courseId);
        setCourse(data);
      } catch (err) {
        setError(err.response?.data?.message || 'Failed to fetch course');
      } finally {
        setLoading(false);
      }
    };

    void fetchCourse();
  }, [courseId]);

  return { course, loading, error };
}