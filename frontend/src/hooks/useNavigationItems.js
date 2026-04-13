import { useMemo } from 'react';
import { useLocation } from 'react-router-dom';
import { useCourses } from './useCourses';
import { BookOpenIcon, FileText, Layers, MessageSquare } from 'lucide-react';

export function useNavigationItems() {
  const location = useLocation();
  const { courses } = useCourses();

  return useMemo(() => {
    const path = location.pathname;

    // Dashboard - Show courses
    if (path === '/dashboard') {
      return [
        {
          title: "Kurser",
          url: "/dashboard",
          icon: BookOpenIcon,
          isActive: true,
          items: courses.map(course => ({
            title: course.name,
            url: `/courses/${course.id}`
          }))
        },
      ];
    }

    // Course page - show course-related links
    if (path.startsWith('/courses/')) {
      const courseId = path.split('/courses/')[1]?.split('/')[0];

      return [
        {
          title: "Kursnavigering",
          url: `/courses/${courseId}`,
          icon: BookOpenIcon,
          isActive: true,
          items: [
            {
              title: "Översikt",
              url: `/courses/${courseId}`,
              isActive: path === `/courses/${courseId}` || path.endsWith('/overview')
            },
            { title: "Uppgifter", url: `/courses/${courseId}/assignments`, icon: FileText },
            { title: "Kursplan", url: `/courses/${courseId}/resources`, icon: Layers },
          ],
        },
      ];
    }

    // Default empty
    return [];
  }, [location.pathname, courses]);
}