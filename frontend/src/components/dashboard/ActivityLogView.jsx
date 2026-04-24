import {useState, useEffect} from 'react';
import {activityLogApi} from '@/api/activityLogs';
import {useAuthContext} from '@/context/AuthContext';
import {Card, CardContent, CardHeader, CardTitle} from '@/components/ui/card';
import {formatDistanceToNow} from 'date-fns';
import {sv} from 'date-fns/locale';
import {Activity, User, FileText, MessageSquare, Briefcase, BookIcon, BookOpenIcon} from 'lucide-react';

export function ActivityLogView({limit = 10, userId, entityType, entityId}) {
    const [logs, setLogs] = useState([]);
    const [loading, setLoading] = useState(true);
    const {user: currentUser} = useAuthContext();

    useEffect(() => {
        let isCurrent = true;

        const fetchLogs = async () => {
            try {
                setLoading(true);
                let data;
                if (userId) {
                    data = await activityLogApi.getUserLogs(userId, 0, limit);
                } else if (entityType && entityId) {
                    data = await activityLogApi.getEntityLogs(entityType, entityId, 0, limit);
                } else if (currentUser?.role?.name === 'ROLE_ADMIN') {
                    data = await activityLogApi.getAllLogs(0, limit);
                } else {
                    data = await activityLogApi.getUserLogs(currentUser.id, 0, limit);
                }
                if (isCurrent) {
                    setLogs(data.content || []);
                }
            } catch (error) {
                if (isCurrent) {
                    console.error('Failed to fetch activity logs:', error);
                    setLogs([]);
                }
            } finally {
                if (isCurrent) {
                    setLoading(false);
                }

            }
        };

        if (currentUser) {
            fetchLogs();
        }
        return () => {
            isCurrent = false;
        };
    }, [currentUser, userId, entityType, entityId, limit]);

    const getActionIcon = (type) => {
        switch (type) {
            case 'USER':
                return <User className="h-4 w-4"/>;
            case 'ASSIGNMENT':
                return <Briefcase className="h-4 w-4"/>;
            case 'COMMENT':
                return <MessageSquare className="h-4 w-4"/>;
            case 'FILE':
                return <FileText className="h-4 w-4"/>;
            case 'COURSE':
                return <BookIcon className="h-4 w-4"/>;
            case 'SUBMISSION':
                return <BookOpenIcon className="h-4 w-4"/>;
            default:
                return <Activity className="h-4 w-4"/>;
        }
    };

    const getActionText = (log) => {
        const action = log.action || '';
        const type = log.entityType || 'POST';
        const details = log.details || {};

        const typeMap = {
            ASSIGNMENT: "uppgift",
            COURSE: "kurs",
            STUDENT: "elev",
            TEACHER: "lärare",
            SCHOOL_CLASS: "klass",
            USER_ASSIGNMENT: "uppgift",
            COMMENT: "kommentar",
            CLASS_ENROLLMENT: "klass",
            SUBMISSION: "inlämning",
        };

        switch (action) {
            case 'LOGIN':
                return 'loggade in';
            case 'REGISTERED':
                return 'registrerade sig';
            case 'CREATED':
                if (type === 'ASSIGNMENT') return `skapade uppgiften "${details.title || 'okänd'}"`;
                if (type === 'COURSE') return `skapade kursen "${details.name || 'okänd'}"`;
                if (type === 'USER') return `skapade användaren "${details.username || 'okänd'}"`;
                return `skapade ${type.toLowerCase()}`;
            case 'UPDATED':
                if (type === 'COURSE') return `uppdaterade kursen "${details.name || 'okänd'}"`;
                if (type === 'USER') return `uppdaterade användaren "${details.username || 'okänd'}"`;
                return `uppdaterade ${type.toLowerCase()}`;
            case 'DELETED': {
                const label = (typeMap[type.toUpperCase()] || type).toLowerCase();
                const candidates = [
                    details.name,
                    details.username,
                    details.title,
                    details.assignmentTitle,
                    details.fileName,
                    details.student,
                    details.class,
                ];
                const resolved = candidates.find((v) => typeof v === 'string' && v.length > 0) ?? 'okänd';
                return `tog bort ${label} (${resolved.toLowerCase()})`;
            }
            case 'ADDED':
                if (type === 'COMMENT') return `kommenterade på "${details.assignmentTitle || 'en uppgift'}"`;
                if (type === 'FILE') return `laddade upp filen "${details.fileName || 'okänd'}"`;
                if (type === 'SUBMISSION') return `lämnade in "${details.assignmentTitle || 'en uppgift'}"`;
                return `lade till ${type.toLowerCase()}`;
            case 'ASSIGNED':
                return `tilldelade "${details.assignmentTitle || 'uppgift'}" till ${details.student || 'en student'}`;
            case 'EVALUATED':
                return `bedömde en inlämning med betyg ${details.grade || '-'}`;
            default:
                return `${action.toLowerCase()} ${type.toLowerCase()}`;
        }
    };

    if (loading) return <p className="text-sm text-muted-foreground p-4">Laddar loggar...</p>;

    return (
        <Card>
            <CardHeader>
                <CardTitle className="text-lg flex items-center gap-2">
                    <Activity className="h-5 w-5"/>
                    Senaste aktivitet
                </CardTitle>
            </CardHeader>
            <CardContent>
                <div className="space-y-4">
                    {logs.length === 0 ? (
                        <p className="text-sm text-muted-foreground">Ingen aktivitet hittades.</p>
                    ) : (
                        logs.map((log) => {
                            const actor =
                                log.actorUsername ||
                                log.details?.username ||
                                'System';

                            return (
                                <div key={log.id}
                                     className="flex gap-3 items-start border-b border-muted pb-3 last:border-0 last:pb-0">
                                    <div className="mt-0.5 bg-muted p-1.5 rounded-full text-muted-foreground">
                                        {getActionIcon(log.entityType)}
                                    </div>
                                    <div className="flex-1 space-y-1">
                                        <p className="text-sm">
                                            <span className="font-semibold">{actor}</span>
                                            {' '}{getActionText(log)}
                                        </p>
                                        <p className="text-xs text-muted-foreground">
                                            {formatDistanceToNow(new Date(log.timestamp), {
                                                addSuffix: true,
                                                locale: sv
                                            })}
                                        </p>
                                    </div>
                                </div>
                            )
                        })
                    )}
                </div>
            </CardContent>
        </Card>
    );
}