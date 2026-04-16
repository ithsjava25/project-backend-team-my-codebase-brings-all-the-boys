import { useState, useEffect } from 'react';
import { courseApi } from '../api/courses';

export function useCourseDetail(courseId) {
  const [course, setCourse] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let isCurrent = true;

    if (!courseId) {
      setCourse(null);
      setError(null);
      setLoading(false);
      return;
    }

    const fetchCourse = async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await courseApi.getCourseById(courseId);
        if (isCurrent) {
          setCourse(data);
        }
      } catch (err) {
        if (isCurrent) {
          setError(err.response?.data?.message || 'Failed to fetch course');
        }
      } finally {
        if (isCurrent) {
          setLoading(false);
        }
      }
    };

    void fetchCourse();

    return () => {
      isCurrent = false;
    };
  }, [courseId]);

  return { course, loading, error };
}