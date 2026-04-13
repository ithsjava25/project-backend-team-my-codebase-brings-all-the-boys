import { GridView } from './CourseListView/GridView';

export default function CourseList({ courses }) {
  const sortedCourses = [...courses]
    .sort((a, b) => a.name.localeCompare(b.name));

  return <GridView courses={sortedCourses} />;
}