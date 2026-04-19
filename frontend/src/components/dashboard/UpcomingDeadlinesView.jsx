import {useState, useEffect} from 'react';
import {dashboardApi} from '@/api/dashboard';
import {Card, CardContent, CardHeader, CardTitle} from '@/components/ui/card';
import {format} from 'date-fns';
import {sv} from 'date-fns/locale';
import {Clock, AlertCircle} from 'lucide-react';
import {Badge} from '@/components/ui/badge';
import {Link} from 'react-router-dom';

export function UpcomingDeadlinesView() {
    const [deadlines, setDeadlines] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchDeadlines = async () => {
            try {
                const data = await dashboardApi.getUpcomingDeadlines();
                setDeadlines(data);
            } catch (error) {
                console.error('Failed to fetch upcoming deadlines:', error);
            } finally {
                setLoading(false);
            }
        };
        fetchDeadlines();
    }, []);

    if (loading) return <p className="text-sm text-muted-foreground p-4">Laddar deadlines...</p>;

    const getStatusBadge = (status) => {
        switch (status) {
            case 'ASSIGNED':
                return <Badge variant="outline">Inte inlämnad</Badge>;
            case 'TURNED_IN':
                return <Badge variant="secondary">Inlämnad</Badge>;
            case 'EVALUATED':
                return <Badge variant="default">Rättad</Badge>;
            default:
                return null;
        }
    };

    return (
        <Card>
            <CardHeader>
                <CardTitle className="text-lg flex items-center gap-2">
                    <Clock className="h-5 w-5 text-orange-500"/>
                    Kommande deadlines
                </CardTitle>
            </CardHeader>
            <CardContent>
                <div className="space-y-4">
                    {deadlines.length === 0 ? (
                        <p className="text-sm text-muted-foreground">Inga kommande deadlines de närmsta 2 veckorna.</p>
                    ) : (
                        deadlines.map((item) => {
                            const date = new Date(item.deadline);
                            const diff = date.getTime() - Date.now();
                            const isUrgent = diff > 0 && diff < 48 * 60 * 60 * 1000;

                            return (
                                <div key={item.assignmentId}
                                     className="flex items-center justify-between border-b border-muted pb-3 last:border-0 last:pb-0">
                                    <div className="space-y-1">
                                        <Link to={`/assignments/${item.assignmentId}`}
                                              className="text-sm font-semibold hover:underline flex items-center gap-2">
                                            {item.title}
                                            {isUrgent && <AlertCircle className="h-3 w-3 text-red-500"/>}
                                        </Link>
                                        <p className="text-xs text-muted-foreground">{item.courseName}</p>
                                    </div>
                                    <div className="text-right space-y-1">
                                        <p className={`text-xs font-medium ${isUrgent ? 'text-red-500' : ''}`}>
                                            {format(date, 'd MMM HH:mm', {locale: sv})}
                                        </p>
                                        {item.status && getStatusBadge(item.status)}
                                    </div>
                                </div>
                            );
                        })
                    )}
                </div>
            </CardContent>
        </Card>
    );
}
