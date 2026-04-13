import {Card, CardAction, CardContent, CardDescription, CardHeader, CardTitle} from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { DayProgress } from './DayProgress';
import {StarIcon} from "lucide-react";

export function CourseCard({ course, status }) {
  const getTimeRemaining = (course) => {
    const now = new Date();
    const start = new Date(course.startDate);
    const end = new Date(course.endDate);

    // Om kursen inte har startat än
    if (now < start) {
      const daysUntilStart = Math.ceil((start - now) / (1000 * 60 * 60 * 24));
      if (daysUntilStart === 0) return 'Startar idag';
      if (daysUntilStart === 1) return 'Startar imorgon';
      return `Startar om ${daysUntilStart} dagar`;
    }

    // Om kursen har startat
    const daysLeft = Math.ceil((end - now) / (1000 * 60 * 60 * 24));
    if (daysLeft < 0) return 'Kursen avslutad';
    if (daysLeft === 0) return 'Sista dagen';
    if (daysLeft === 1) return '1 dag kvar';
    return `${daysLeft} dagar kvar av kursen`;
  };

  const getBadgeInfo = () => {
    switch (status) {
      case 'not-started':
        return { label: 'Ej påbörjad', variant: 'yellow' };
      case 'completed':
        return { label: 'Avslutad', variant: 'secondary' };
      case 'active':
      default:
        return { label: 'Pågående', variant: 'default' };
    }
  };

  const badgeInfo = getBadgeInfo();

  return (
    <Card className={hasDetailedData ? "" : "cursor-pointer hover:bg-accent"}>
        <CardHeader>
          <div className="flex items-start justify-between">
            <div>
              <CardTitle className="flex items-center gap-1">
                {course.isFavorite && <StarIcon className="h-5 w-5 text-yellow-500 fill-yellow-500" />}
                {!course.isFavorite && <StarIcon className="h-6 w-6 text-gray-400" />}
                {course.name}
              </CardTitle>
              <CardDescription>
                {course.class}
              </CardDescription>
            </div>
              <CardAction className="space-y-2">
                <div>
                  {course.completedAll && (
                    <Badge variant="green">Fullbordad</Badge>
                  )}
                  {status === 'completed' && !course.completedAll && (
                    <Badge variant="destructive">Ej fullbordad</Badge>
                  )}
                </div>
                <div>
                  <Badge variant={badgeInfo.variant}>
                    {badgeInfo.label}
                  </Badge>
                </div>
              </CardAction>
          </div>
        </CardHeader>
        <CardContent>
          <div className="space-y-2 -mt-2">
            <div className="flex justify-between text-xs text-muted-foreground">
              <span>{getTimeRemaining(course)}</span>
            </div>
            <DayProgress course={course} />
          </div>
        </CardContent>
        <CardContent className="text-foreground">
          <Badge variant="outline" className="w-full rounded-lg text-center text-sm py-5 mt-4">
            {course.completed ?? 0}/{course.assignments} avklarade moment
          </Badge>
        </CardContent>
    </Card>

  );
}