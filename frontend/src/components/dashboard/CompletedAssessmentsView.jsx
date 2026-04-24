import {useState, useEffect} from 'react';
import {userAssignmentApi} from '@/api/userAssignments';
import {Card, CardContent, CardHeader, CardTitle} from '@/components/ui/card';
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from '@/components/ui/table';
import {Badge} from '@/components/ui/badge';
import {Button} from '@/components/ui/button';
import {Link} from 'react-router-dom';
import {format} from 'date-fns';
import {sv} from 'date-fns/locale';
import {CheckCircle2} from 'lucide-react';

export function CompletedAssessmentsView() {
    const [assessments, setAssessments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const size = 10;

    const fetchAssessments = async (signal) => {
        try {
            setLoading(true);
            setError(null);
            const data = await userAssignmentApi.getEvaluatedAssignments(page, size, signal);
            
            if (!signal?.aborted) {
                // If backend returns a Page object, use .content, otherwise use data directly
                const content = data.content !== undefined ? data.content : data;
                setAssessments(content || []);
                setTotalPages(data.totalPages || 0);
            }
        } catch (err) {
            if (err.name === 'CanceledError' || err.name === 'AbortError') return;
            console.error('Failed to fetch completed assessments:', err);
            if (!signal?.aborted) {
                setError(err.response?.data?.message || 'Kunde inte hämta bedömningar.');
                setAssessments([]);
            }
        } finally {
            if (!signal?.aborted) {
                setLoading(false);
            }
        }
    };

    useEffect(() => {
        const controller = new AbortController();
        fetchAssessments(controller.signal);
        return () => controller.abort();
    }, [page]);

    if (loading && assessments.length === 0) return <p className="text-sm text-muted-foreground p-4">Laddar bedömningar...</p>;

    return (
        <Card>
            <CardHeader className="flex flex-row items-center justify-between">
                <CardTitle className="text-lg flex items-center gap-2">
                    <CheckCircle2 className="h-5 w-5 text-green-500"/>
                    Utförda bedömningar
                </CardTitle>
                {totalPages > 1 && (
                    <div className="flex items-center gap-2">
                        <Button 
                            variant="outline" 
                            size="sm" 
                            onClick={() => setPage(p => Math.max(0, p - 1))}
                            disabled={page === 0 || loading}
                        >
                            Föregående
                        </Button>
                        <span className="text-xs text-muted-foreground">
                            Sida {page + 1} av {totalPages}
                        </span>
                        <Button 
                            variant="outline" 
                            size="sm" 
                            onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}
                            disabled={page >= totalPages - 1 || loading}
                        >
                            Nästa
                        </Button>
                    </div>
                )}
            </CardHeader>
            <CardContent>
                {error && <p className="text-sm text-destructive mb-4">{error}</p>}
                
                {assessments.length === 0 ? (
                    <p className="text-sm text-muted-foreground italic">Du har inte bedömt några uppgifter än.</p>
                ) : (
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead>Student</TableHead>
                                <TableHead>Uppgift</TableHead>
                                <TableHead>Betyg</TableHead>
                                <TableHead>Datum</TableHead>
                                <TableHead className="text-right">Åtgärd</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {assessments.map((ua) => (
                                <TableRow key={ua.id}>
                                    <TableCell className="font-medium">
                                        {ua.student ? (
                                            <Link to={`/profile/${ua.student.id}`} className="hover:underline text-primary">
                                                {ua.student.username}
                                            </Link>
                                        ) : (
                                            <span className="text-muted-foreground italic">Okänd student</span>
                                        )}
                                    </TableCell>
                                    <TableCell className="truncate max-w-[150px]" title={ua.assignmentTitle}>
                                        {ua.assignmentTitle || 'Okänd uppgift'}
                                    </TableCell>
                                    <TableCell>
                                        <Badge variant="default" className="font-bold">{ua.grade || '-'}</Badge>
                                    </TableCell>
                                    <TableCell className="text-xs text-muted-foreground">
                                        {ua.turnedInAt ? format(new Date(ua.turnedInAt), 'PP', {locale: sv}) : '-'}
                                    </TableCell>
                                    <TableCell className="text-right">
                                        {ua.assignmentId && ua.student ? (
                                            <Button asChild size="sm" variant="ghost">
                                                <Link to={`/assignments/${ua.assignmentId}/grade/${ua.student.id}`}>Ändra</Link>
                                            </Button>
                                        ) : (
                                            <Button size="sm" variant="ghost" disabled>Ändra</Button>
                                        )}
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                )}
            </CardContent>
        </Card>
    );
}
