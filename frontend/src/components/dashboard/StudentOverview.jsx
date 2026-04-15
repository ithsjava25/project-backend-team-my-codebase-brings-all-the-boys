import { CourseListView } from './CourseListView';

export default function StudentOverview({ courses }) {
  return <CourseListView courses={courses} view="grid" role="student" />;
}