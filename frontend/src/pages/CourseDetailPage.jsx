import { useParams } from 'react-router-dom';
import { useCourseDetail } from '@/hooks/useCourseDetail';
import { mapToCourseDetailFormat } from '@/mappers/courseMapper';

export default function CourseDetailPage() {
  const { courseId } = useParams();
  const { course, loading, error } = useCourseDetail(courseId);

  if (loading) return <div>Laddar kurs...</div>;
  if (error) return <div>Ett fel uppstod: {error}</div>;

  const courseData = mapToCourseDetailFormat(course);

  return (
    <div className="space-y-6">
      <h1>{courseData.name}</h1>
      <p>{courseData.description}</p>
      <p>Klass: {courseData.schoolClassName}</p>
      {/* TODO: Mer content */}
    </div>
  );
}