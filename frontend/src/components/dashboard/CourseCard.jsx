import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Progress } from '@/components/ui/progress';
import {StarIcon} from "lucide-react";

export function CourseCard({ course, status }) {
  const getTimeProgress = (course) => {
    const now = new Date();
    const start = new Date(course.startDate);
    const end = new Date(course.endDate);
    const total = end - start;
    const elapsed = now - start;
    return Math.max(0, Math.min(100, Math.round((elapsed / total) * 100)));
  };

  const getTimeRemaining = (course) => {
    const now = new Date();
    const end = new Date(course.endDate);
    const daysLeft = Math.ceil((end - now) / (1000 * 60 * 60 * 24));

    if (daysLeft < 0) return 'Kursen avslutad';
    if (daysLeft === 0) return 'Sista dagen';
    if (daysLeft === 1) return '1 dag kvar';
    return `${daysLeft} dagar kvar`;
  };

  const getBadgeInfo = () => {
    switch (status) {
      case 'not-started':
        return { label: 'Ej påbörjad', variant: 'yellow' };
      case 'completed':
        return { label: 'Avslutad', variant: 'secondary' };
      case 'active':
      default:
        return { label: 'Aktiv', variant: 'green' };
    }
  };

  const badgeInfo = getBadgeInfo();

  return (
    <Card>
      <CardHeader>
        <div className="flex items-start justify-between">
          <div>
            <CardTitle>{course.name}</CardTitle>
            <CardDescription>{course.class}</CardDescription>
          </div>
          <div className="flex items-center gap-2">
            <Badge variant={badgeInfo.variant}>
              {badgeInfo.label}
            </Badge>
            {course.isFavorite && <StarIcon className="h-6 w-6 text-yellow-500 fill-yellow-500" />}
            {!course.isFavorite && <StarIcon className="h-6 w-6 text-gray-400" />}
          </div>


        </div>
      </CardHeader>
      <CardContent>
        <div className="space-y-2">
          <div className="flex justify-between text-sm">
            <span>Tid kvar</span>
            <span>{getTimeRemaining(course)}</span>
          </div>
          <Progress value={getTimeProgress(course)} />
          <p className="text-sm text-muted-foreground">{course.assignments} uppgifter</p>
        </div>
      </CardContent>
    </Card>
  );
}