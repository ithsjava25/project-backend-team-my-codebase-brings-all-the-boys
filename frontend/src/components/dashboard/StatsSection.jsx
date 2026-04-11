import { BookOpen, Users, FileText, Clock, Activity, Shield, CheckCircle2, Calendar } from 'lucide-react';
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
          { label: 'Mina kurser', value: '2', icon: BookOpen },
          { label: 'Totalt studenter', value: '60', icon: Users },
          { label: 'Ej rättade', value: '2', icon: FileText },
          { label: 'Senaste aktivitet', value: '1h', icon: Clock },
        ];
      default:
        return [
          { label: 'Aktiva kurser', value: '3', icon: BookOpen },
          { label: 'Kommande uppgifter', value: '3', icon: Calendar },
          { label: 'Kommande inlämningsdatum', value: '2 dagar', icon: Clock },
          { label: 'Avslutade uppgifter', value: '12', icon: CheckCircle2 },
        ];
    }
  };

  const stats = getStats();

  return (
    <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
      {stats.map((stat) => (
        <Card key={stat.label} size="sm">
          <CardHeader className="flex flex-row items-center justify-between pb-2">
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