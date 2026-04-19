import {useState, useEffect} from 'react';
import {
    Users,
    ClockIcon,
    BookOpenIcon,
    BookIcon,
    GraduationCap
} from 'lucide-react';
import {Card, CardContent, CardHeader, CardTitle} from '@/components/ui/card';
import {dashboardApi} from '@/api/dashboard';
import {format} from 'date-fns';
import {sv} from 'date-fns/locale';
import {Button} from '@/components/ui/button';

export default function StatsSection({role, onSelectTab}) {
    const [stats, setStats] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const fetchStats = async () => {
        try {
            setLoading(true);
            setError(null);
            const data = await dashboardApi.getStats();
            setStats(data.stats);
        } catch (err) {
            console.error('Failed to fetch stats:', err);
            setError(err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchStats();
    }, []);

    const getStatsConfig = () => {
        if (!stats) return [];

        switch (role) {
            case 'ROLE_ADMIN':
                return [
                    {label: 'Totalt användare', value: stats.totalUsers, icon: Users, tab: 'users'},
                    {label: 'Lärare', value: stats.totalTeachers, icon: GraduationCap, tab: 'users'},
                    {label: 'Studenter', value: stats.totalStudents, icon: Users, tab: 'users'},
                    {label: 'Kurser', value: stats.totalCourses, icon: BookIcon, tab: 'courses'},
                ];
            case 'ROLE_TEACHER':
                return [
                    {label: 'Att betygsätta', value: stats.pendingGrading, icon: BookOpenIcon, tab: 'grading'},
                    {label: 'Mina kurser', value: stats.activeCourses, icon: BookIcon, tab: 'courses'},
                ];
            case 'ROLE_STUDENT':
                return [
                    {
                        label: 'Pågående uppgifter',
                        value: stats.pendingAssignments,
                        icon: BookOpenIcon,
                        tab: 'assignments'
                    },
                    {
                        label: 'Nästa deadline',
                        value: stats.nextDeadline
                            ? format(new Date(stats.nextDeadline), 'd MMM', {locale: sv})
                            : 'Ingen',
                        icon: ClockIcon
                    },
                ];
            default:
                return [];
        }
    };

    const config = getStatsConfig();

    const handleClick = (tab) => {
        if (!tab || !onSelectTab) return;
        onSelectTab(tab);
    };

    // ✅ Loading
    if (loading) {
        return (
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                {[1, 2, 3, 4].map(i => (
                    <Card key={i} className="animate-pulse h-20"/>
                ))}
            </div>
        );
    }

    // ✅ Error state with retry
    if (error) {
        return (
            <div className="flex items-center justify-between p-4 border rounded">
                <span className="text-destructive">Kunde inte ladda statistik</span>
                <Button onClick={fetchStats} size="sm">Försök igen</Button>
            </div>
        );
    }

    return (
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            {config.map((stat) => (
                <Card
                    key={stat.label}
                    className={stat.tab ? "cursor-pointer hover:bg-muted/50 transition" : ""}
                    onClick={stat.tab ? () => handleClick(stat.tab) : undefined}
                    role={stat.tab ? "button" : undefined}
                    tabIndex={stat.tab ? 0 : undefined}
                    onKeyDown={stat.tab ? (event) => {
                        if (event.key === 'Enter' || event.key === ' ') {
                            event.preventDefault();
                            handleClick(stat.tab);
                        }
                    } : undefined}
                >
                    <CardHeader className="flex flex-row items-center justify-between pb-2">
                        <CardTitle className="text-sm font-medium">
                            {stat.label}
                        </CardTitle>
                        <stat.icon className="h-4 w-4 text-muted-foreground"/>
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">{stat.value}</div>
                    </CardContent>
                </Card>
            ))}
        </div>
    );
}