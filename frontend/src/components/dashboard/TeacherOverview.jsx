import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { ActivityLogView } from './ActivityLogView';

export default function TeacherOverview() {

  return (
    <div className="space-y-6">
      <ActivityLogView limit={10} />
    </div>
  );
}