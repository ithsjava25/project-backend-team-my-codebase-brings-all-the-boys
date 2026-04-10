import { useState } from 'react';
import { GridView } from './CourseListView/GridView';
import { TableView } from './CourseListView/TableView';

export default function CourseList({ courses }) {
  const [viewMode, setViewMode] = useState('grid');

  const now = new Date();

  const notStartedCourses = courses.filter(course => new Date(course.startDate) > now);
  const activeCourses = courses.filter(course => {
    const start = new Date(course.startDate);
    const end = new Date(course.endDate);
    return now >= start && now <= end && course.progress < 100;
  });
  const completedCourses = courses.filter(course => course.progress === 100 || new Date(course.endDate) < now);

  if (viewMode === 'table') {
    return <TableView
      notStartedCourses={notStartedCourses}
      activeCourses={activeCourses}
      completedCourses={completedCourses}
      onViewChange={setViewMode}
    />;
  }

  return <GridView
    notStartedCourses={notStartedCourses}
    activeCourses={activeCourses}
    completedCourses={completedCourses}
    onViewChange={setViewMode}
  />;
}
