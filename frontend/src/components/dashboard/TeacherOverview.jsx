import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { ActivityLogView } from './ActivityLogView';
import { PendingSubmissionsView } from './PendingSubmissionsView';
import { UpcomingDeadlinesView } from './UpcomingDeadlinesView';

export default function TeacherOverview() {

  return (
    <div className="space-y-6">
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <PendingSubmissionsView />
        <UpcomingDeadlinesView />
      </div>

      <ActivityLogView limit={10} />
    </div>
  );
}