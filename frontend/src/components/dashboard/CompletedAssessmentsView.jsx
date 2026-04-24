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

    const fetchAssessments = async () => {
        try {
            setLoading(true);
            setError(null);
            const data = await userAssignmentApi.getEvaluatedAssignments();
            setAssessments(data);
        } catch (err) {
            console.error('Failed to fetch completed assessments:', err);
            setError(err.response?.data?.message || 'Kunde inte hämta bedömningar.');
            setAssessments([]);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchAssessments();
    }, []);

    if (loading && assessments.length === 0) return <p className="text-sm text-muted-foreground p-4">Laddar bedömningar...</p>;

    return (
        <Card>
            <CardHeader>
                <CardTitle className="text-lg flex items-center gap-2">
                    <CheckCircle2 className="h-5 w-5 text-green-500"/>
                    Utförda bedömningar
                </CardTitle>
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
                                        <Link to={`/profile/${ua.student.id}`} className="hover:underline text-primary">
                                            {ua.student.username}
                                        </Link>
                                    </TableCell>
                                    <TableCell className="truncate max-w-[150px]" title={ua.assignmentTitle}>
                                        {ua.assignmentTitle}
                                    </TableCell>
                                    <TableCell>
                                        <Badge variant="default" className="font-bold">{ua.grade}</Badge>
                                    </TableCell>
                                    <TableCell className="text-xs text-muted-foreground">
                                        {ua.turnedInAt ? format(new Date(ua.turnedInAt), 'PP', {locale: sv}) : '-'}
                                    </TableCell>
                                    <TableCell className="text-right">
                                        <Button asChild size="sm" variant="ghost">
                                            <Link to={`/assignments/${ua.assignmentId}/grade/${ua.student.id}`}>Ändra</Link>
                                        </Button>
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
