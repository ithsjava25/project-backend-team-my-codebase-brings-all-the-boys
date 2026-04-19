import { useState, useEffect } from 'react';
import {
  Users,
  ClockIcon,
  BookOpenIcon,
  BookIcon,
  GraduationCap
} from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { dashboardApi } from '@/api/dashboard';
import { format } from 'date-fns';
import { sv } from 'date-fns/locale';
import { useNavigate } from 'react-router-dom';

export default function StatsSection({ role }) {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const data = await dashboardApi.getStats();
        setStats(data.stats);
      } catch (error) {
        console.error('Failed to fetch stats:', error);
      } finally {
        setLoading(false);
      }
    };
    fetchStats();
  }, []);

  const getStatsConfig = () => {
    if (loading || !stats) return [];

    switch (role) {
      case 'ROLE_ADMIN':
        return [
          { label: 'Totalt användare', value: stats.totalUsers, icon: Users, path: '/admin/users' },
          { label: 'Lärare', value: stats.totalTeachers, icon: GraduationCap, path: '/admin/users' },
          { label: 'Studenter', value: stats.totalStudents, icon: Users, path: '/admin/users' },
          { label: 'Kurser', value: stats.totalCourses, icon: BookIcon, path: '/admin/courses' },
        ];
      case 'ROLE_TEACHER':
        return [
          { label: 'Att betygsätta', value: stats.pendingGrading, icon: BookOpenIcon, path: '#grading' },
          { label: 'Mina kurser', value: stats.activeCourses, icon: BookIcon, path: '#courses' },
        ];
      case 'ROLE_STUDENT':
        return [
          { label: 'Pågående uppgifter', value: stats.pendingAssignments, icon: BookOpenIcon, path: '#assignments' },
          { 
            label: 'Nästa deadline', 
            value: stats.nextDeadline ? format(new Date(stats.nextDeadline), 'd MMM', { locale: sv }) : 'Ingen', 
            icon: ClockIcon 
          },
        ];
      default:
        return [];
    }
  };

  const config = getStatsConfig();

  const handleCardClick = (path) => {
    if (!path) return;
    if (path.startsWith('#')) {
      // For now, these might be tabs on the dashboard
      // You could trigger a tab change if you lift state, but for now we just link if it's a real path
      return;
    }
    navigate(path);
  };

  if (loading) {
    return (
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        {[1, 2, 3, 4].slice(0, role === 'ROLE_ADMIN' ? 4 : 2).map((i) => (
          <Card key={i} size="sm" className="animate-pulse">
            <CardHeader className="h-20" />
          </Card>
        ))}
      </div>
    );
  }

  return (
    <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
      {config.map((stat) => (
        <Card 
          key={stat.label} 
          size="sm" 
          className={stat.path && !stat.path.startsWith('#') ? "hover:cursor-pointer hover:bg-muted/50 transition-colors" : ""}
          onClick={() => handleCardClick(stat.path)}
        >
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