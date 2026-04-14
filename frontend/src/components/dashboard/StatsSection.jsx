import {
  BookOpen,
  Users,
  FileText,
  Activity,
  ClockIcon,
  BookOpenIcon
} from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';

export default function StatsSection({ role, user }) {
  const getStats = () => {
    switch (role) {
      case 'ROLE_ADMIN':
        return [
          { label: 'Totalt användare', value: '156', icon: Users },
          { label: 'Aktiva kurser', value: '8', icon: BookOpen },
          { label: 'Uppgifter', value: '45', icon: FileText },
          { label: 'Activity logs', value: '1.2K', icon: Activity },
        ];
      case 'ROLE_TEACHER':
        return [
          { label: 'Inlämningar att betygsätta', value: '2', icon: BookOpenIcon },
        ];
      default:
        return [
          { label: 'Närmaste inlämningsdatum', value: '24/7-26', test: 'test', icon: ClockIcon },
        ];
    }
  };

  const stats = getStats();

  return (
    <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
      {stats.map((stat) => (
        <Card key={stat.label} size="sm">
          <CardHeader className="flex flex-row items-center justify-between">
            <CardTitle className="text-sm font-medium">{stat.label}</CardTitle>
            <stat.icon className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{stat.value}</div>
          </CardContent>
        </Card>
      ))}
    </div>
  );
}