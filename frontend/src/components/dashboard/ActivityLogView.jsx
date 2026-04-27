import {useState, useEffect} from 'react';
import {activityLogApi} from '@/api/activityLogs';
import {userApi} from '@/api/users';
import {useAuthContext} from '@/context/AuthContext';
import {Card, CardContent, CardHeader, CardTitle} from '@/components/ui/card';
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from '@/components/ui/select';
import {Button} from '@/components/ui/button';
import {formatDistanceToNow} from 'date-fns';
import {sv} from 'date-fns/locale';
import {Activity, User, FileText, MessageSquare, Briefcase, BookIcon, BookOpenIcon, Filter, X} from 'lucide-react';

export function ActivityLogView({limit = 10, userId: initialUserId, entityType: initialEntityType, entityId}) {
    const [logs, setLogs] = useState([]);
    const [loading, setLoading] = useState(true);
    const [allUsers, setAllUsers] = useState([]);
    const {user: currentUser} = useAuthContext();

    // Filters
    const [filters, setFilters] = useState({
        userId: initialUserId || '',
        action: '',
        entityType: initialEntityType || '',
        status: ''
    });

    const isAdmin = currentUser?.role?.name === 'ROLE_ADMIN';

    useEffect(() => {
        const controller = new AbortController();
        if (isAdmin) {
            const fetchAllUsers = async () => {
                try {
                    let allFetchedUsers = [];
                    let page = 0;
                    let totalPages = 1;

                    while (page < totalPages && !controller.signal.aborted) {
                        const data = await userApi.getAllUsers({page, size: 100}, controller.signal);
                        if (!controller.signal.aborted) {
                            allFetchedUsers = [...allFetchedUsers, ...(data.content || [])];
                            totalPages = data.totalPages || 0;
                            page++;
                        }
                    }
                    if (!controller.signal.aborted) {
                        setAllUsers(allFetchedUsers);
                    }
                } catch (err) {
                    if (err.name !== 'AbortError' && err.name !== 'CanceledError') {
                        console.error('Failed to fetch users for filtering:', err);
                    }
                }
            };
            fetchAllUsers();
        }
        return () => controller.abort();
    }, [isAdmin]);

    const fetchLogs = async (signal) => {
        try {
            setLoading(true);
            let data;

            if (isAdmin && !initialUserId && !entityId) {
                // Admin global view - supports all filters
                data = await activityLogApi.getAllLogs(0, limit, filters, signal);
            } else if (filters.userId || initialUserId) {
                // User-scoped view - route through getAllLogs to preserve action/type filters
                const combinedFilters = {
                    ...filters,
                    userId: filters.userId || initialUserId
                };
                data = await activityLogApi.getAllLogs(0, limit, combinedFilters, signal);
            } else if (filters.entityType || initialEntityType) {
                // Entity-scoped view
                const combinedFilters = {
                    ...filters,
                    entityType: filters.entityType || initialEntityType,
                    entityId
                };
                data = await activityLogApi.getAllLogs(0, limit, combinedFilters, signal);
            } else {
                // Default: current user's logs
                data = await activityLogApi.getUserLogs(currentUser.id, 0, limit, signal);
            }

            if (!signal?.aborted) {
                setLogs(data.content || []);
            }
        } catch (error) {
            if (error.name === 'CanceledError' || error.name === 'AbortError') return;
            console.error('Failed to fetch activity logs:', error);
            if (!signal?.aborted) {
                setLogs([]);
            }
        } finally {
            if (!signal?.aborted) {
                setLoading(false);
            }
        }
    };

    useEffect(() => {
        const controller = new AbortController();
        if (currentUser) {
            fetchLogs(controller.signal);
        }
        return () => controller.abort();
    }, [currentUser, filters, limit, initialUserId, initialEntityType, entityId]);

    const resetFilters = () => {
        setFilters({
            userId: initialUserId || '',
            action: '',
            entityType: initialEntityType || '',
            status: ''
        });
    };

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
            CLASS_ENROLLMENT: "enrollment", // Internal label, used in logic below
            SUBMISSION: "inlämning",
            USER: "användare",
            FILE: "fil",
        };

        const getEntityLabel = (t) => (typeMap[t.toUpperCase()] || t).toLowerCase();

        switch (action) {
            case 'LOGIN':
                return 'loggade in';
            case 'REGISTERED':
                return 'registrerade sig';
            case 'CREATED':
                if (type === 'ASSIGNMENT') return `skapade uppgiften "${details.title || 'okänd'}"`;
                if (type === 'COURSE') return `skapade kursen "${details.name || 'okänd'}"`;
                if (type === 'USER') return `skapade användaren "${details.username || 'okänd'}"`;
                return `skapade ${getEntityLabel(type)}`;
            case 'UPDATED':
                if (type === 'COURSE') return `uppdaterade kursen "${details.name || 'okänd'}"`;
                if (type === 'USER') return `uppdaterade användaren "${details.username || 'okänd'}"`;
                if (type === 'CLASS_ENROLLMENT') {
                    return `uppdaterade rollen för ${details.enrolledUser || 'en användare'} till ${details.role || 'okänd'}`;
                }
                return `uppdaterade ${getEntityLabel(type)}`;
            case 'DELETED': {
                const label = getEntityLabel(type);
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
                return `tog bort ${label === 'enrollment' ? 'medlemskap' : label} (${resolved})`;
            }
            case 'REMOVED':
                if (type === 'CLASS_ENROLLMENT') {
                    return `tog bort ${details.removedUser || 'en användare'} från klassen ${details.class || ''}`.trim();
                }
                return `tog bort ${getEntityLabel(type)}`;
            case 'ADDED':
                if (type === 'COMMENT') return `kommenterade på "${details.assignmentTitle || 'en uppgift'}"`;
                if (type === 'FILE') return `laddade upp filen "${details.fileName || 'okänd'}"`;
                if (type === 'SUBMISSION') return `lämnade in "${details.assignmentTitle || 'en uppgift'}"`;
                if (type === 'CLASS_ENROLLMENT') {
                    return `lade till ${details.enrolledUser || 'en användare'} i klassen ${details.class || ''} som ${details.role || 'medlem'}`.trim();
                }
                return `lade till ${getEntityLabel(type)}`;
            case 'ASSIGNED':
                return `tilldelade "${details.assignmentTitle || 'uppgift'}" till ${details.student || 'en student'}`;
            case 'EVALUATED':
                return `bedömde en inlämning med betyg ${details.grade || '-'}`;
            default:
                return `${action.toLowerCase()} ${getEntityLabel(type)}`;
        }
    };

    if (loading) return <p className="text-sm text-muted-foreground p-4">Laddar loggar...</p>;

    return (
        <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-4">
                <CardTitle className="text-lg flex items-center gap-2">
                    <Activity className="h-5 w-5"/>
                    Senaste aktivitet
                </CardTitle>
                {isAdmin && (
                    <Button variant="outline" size="sm" onClick={resetFilters} className="h-8 gap-2">
                        <X className="h-3 w-3"/> Återställ filter
                    </Button>
                )}
            </CardHeader>
            <CardContent className="space-y-6">
                {isAdmin && !initialUserId && !entityId && (
                    <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 pb-4 border-b">
                        <div className="space-y-1.5">
                            <label className="text-xs font-medium text-muted-foreground flex items-center gap-1">
                                <User className="h-3 w-3"/> Användare
                            </label>
                            <Select
                                value={filters.userId || 'ALL'}
                                onValueChange={(v) => setFilters(f => ({...f, userId: v === 'ALL' ? '' : v}))}
                            >
                                <SelectTrigger className="h-9">
                                    <SelectValue placeholder="Alla användare"/>
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="ALL">Alla användare</SelectItem>
                                    {allUsers.map(u => (
                                        <SelectItem key={u.id} value={u.id}>{u.username}</SelectItem>
                                    ))}
                                </SelectContent>
                            </Select>
                        </div>

                        <div className="space-y-1.5">
                            <label className="text-xs font-medium text-muted-foreground flex items-center gap-1">
                                <Filter className="h-3 w-3"/> Åtgärd
                            </label>
                            <Select
                                value={filters.action || 'ALL'}
                                onValueChange={(v) => setFilters(f => ({...f, action: v === 'ALL' ? '' : v}))}
                            >
                                <SelectTrigger className="h-9">
                                    <SelectValue placeholder="Alla åtgärder"/>
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="ALL">Alla åtgärder</SelectItem>
                                    <SelectItem value="CREATED">Skapad</SelectItem>
                                    <SelectItem value="UPDATED">Uppdaterad</SelectItem>
                                    <SelectItem value="DELETED">Borttagen</SelectItem>
                                    <SelectItem value="ADDED">Tillagd</SelectItem>
                                    <SelectItem value="ASSIGNED">Tilldelad</SelectItem>
                                    <SelectItem value="EVALUATED">Bedömd</SelectItem>
                                    <SelectItem value="LOGIN">Inloggning</SelectItem>
                                </SelectContent>
                            </Select>
                        </div>

                        <div className="space-y-1.5">
                            <label className="text-xs font-medium text-muted-foreground flex items-center gap-1">
                                <Activity className="h-3 w-3"/> Typ
                            </label>
                            <Select
                                value={filters.entityType || 'ALL'}
                                onValueChange={(v) => setFilters(f => ({...f, entityType: v === 'ALL' ? '' : v}))}
                            >
                                <SelectTrigger className="h-9">
                                    <SelectValue placeholder="Alla typer"/>
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="ALL">Alla typer</SelectItem>
                                    <SelectItem value="USER">Användare</SelectItem>
                                    <SelectItem value="COURSE">Kurs</SelectItem>
                                    <SelectItem value="SCHOOL_CLASS">Klass</SelectItem>
                                    <SelectItem value="ASSIGNMENT">Uppgift</SelectItem>
                                    <SelectItem value="SUBMISSION">Inlämning</SelectItem>
                                    <SelectItem value="COMMENT">Kommentar</SelectItem>
                                </SelectContent>
                            </Select>
                        </div>
                    </div>
                )}

                <div className="space-y-4 pt-2">
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