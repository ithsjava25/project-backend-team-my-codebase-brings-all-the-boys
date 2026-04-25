import {useParams, Link, useNavigate} from 'react-router-dom';
import {useState, useEffect} from 'react';
import {useAssignmentDetail} from '@/hooks/useAssignmentDetail';
import {userAssignmentApi} from '@/api/userAssignments';
import {CommentSection} from '@/components/dashboard/CommentSection';
import {FileSection} from '@/components/dashboard/FileSection';
import {Card, CardContent, CardHeader, CardTitle} from '@/components/ui/card';
import {Badge} from '@/components/ui/badge';
import {ArrowLeft, Calendar, User, Edit, ClipboardCheck, Send} from 'lucide-react';
import {Button} from '@/components/ui/button';
import {useAuthContext} from '@/context/AuthContext';
import {Tabs, TabsContent, TabsList, TabsTrigger} from '@/components/ui/tabs';
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from '@/components/ui/table';
import {Textarea} from '@/components/ui/textarea';

export default function AssignmentDetailPage() {
    const {assignmentId} = useParams();
    const {assignment, loading, error} = useAssignmentDetail(assignmentId);
    const {user} = useAuthContext();
    const navigate = useNavigate();

    const [userAssignments, setUserAssignments] = useState([]);
    const [myUserAssignment, setMyUserAssignment] = useState(null);
    const [myAssignmentLoading, setMyAssignmentLoading] = useState(false);
    const [myAssignmentError, setMyAssignmentError] = useState(null);
    const [loadingSubmissions, setLoadingSubmissions] = useState(false);
    const [submissionError, setSubmissionError] = useState(null);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [submissionContent, setSubmissionContent] = useState('');

    const isAdmin = user?.role?.name === 'ROLE_ADMIN';
    const isTeacher = user?.role?.name === 'ROLE_TEACHER';
    const isStudent = user?.role?.name === 'ROLE_STUDENT';
    const canManageSubmissions = isAdmin || isTeacher;

    useEffect(() => {
        if (canManageSubmissions && assignment?.id) {
            const fetchSubmissions = async () => {
                try {
                    setLoadingSubmissions(true);
                    setSubmissionError(null);
                    const data = await userAssignmentApi.getByAssignment(assignment.id);
                    setUserAssignments(data);
                } catch (err) {
                    console.error('Failed to fetch submissions:', err);
                    setSubmissionError('Kunde inte hämta studentinlämningar.');
                } finally {
                    setLoadingSubmissions(false);
                }
            };
            fetchSubmissions();
        }
    }, [assignment?.id, canManageSubmissions]);

    useEffect(() => {
        if (isStudent && assignment?.id) {
            const fetchMyAssignment = async () => {
                try {
                    setMyAssignmentLoading(true);
                    setMyAssignmentError(null);
                    const data = await userAssignmentApi.getMyAssignment(assignment.id);
                    setMyUserAssignment(data);
                    if (data?.submissions?.length > 0) {
                        setSubmissionContent(data.submissions[data.submissions.length - 1].content || '');
                    }
                } catch (err) {
                    console.error('Failed to fetch my assignment:', err);
                    setMyAssignmentError('Kunde inte hämta din inlämning.');
                } finally {
                    setMyAssignmentLoading(false);
                }
            };
            fetchMyAssignment();
        }
    }, [assignment?.id, isStudent]);

    const handleSubmission = async () => {
        if (!myUserAssignment || isSubmitting) return;

        try {
            setIsSubmitting(true);
            const updated = await userAssignmentApi.submit(myUserAssignment.id, {
                content: submissionContent,
                fileS3Keys: [] // Files are already attached via FileSection in the current implementation
            });
            setMyUserAssignment(updated);
            alert('Din inlämning har skickats!');
        } catch (err) {
            console.error('Submission failed:', err);
            alert(err.response?.data?.message || 'Inlämningen misslyckades.');
        } finally {
            setIsSubmitting(false);
        }
    };

    const formatDate = (dateString) => {
        if (!dateString) return '-';
        const date = new Date(dateString);
        return date.toLocaleDateString('sv-SE', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    const getStatusLabel = (status) => {
        switch (status) {
            case 'OPEN':
                return 'Öppen';
            case 'CLOSED':
                return 'Stängd';
            case 'CREATED':
                return 'Skapad';
            default:
                return status;
        }
    };

    const getStatusVariant = (status) => {
        switch (status) {
            case 'OPEN':
                return 'default';
            case 'CLOSED':
                return 'secondary';
            case 'CREATED':
                return 'outline';
            default:
                return 'outline';
        }
    };

    const getUserAssignmentStatusLabel = (status) => {
        switch (status) {
            case 'ASSIGNED': return 'Tilldelad';
            case 'TURNED_IN': return 'Inlämnad';
            case 'EVALUATED': return 'Bedömd';
            default: return status;
        }
    };

    if (loading) return <div className="p-8">Laddar uppgift...</div>;
    if (error) return <div className="p-8 text-destructive">Fel: {error}</div>;
    if (!assignment) return <div className="p-8">Uppgiften hittades inte.</div>;

    return (
        <div className="space-y-6">
            <div className="space-y-2">
                <button
                    type="button"
                    onClick={() => navigate(-1)}
                    className="inline-flex items-center text-sm text-muted-foreground hover:text-foreground"
                >
                    <ArrowLeft className="mr-2 h-4 w-4"/>
                    Tillbaka
                </button>

                <div className="flex items-center justify-between">
                    <h1 className="text-3xl font-bold">{assignment.title}</h1>
                    <div className="flex items-center gap-4">
                        <Badge variant={getStatusVariant(assignment.status)}>
                            {getStatusLabel(assignment.status)}
                        </Badge>
                        {isStudent && myUserAssignment && (
                            <Badge variant={myUserAssignment.status === 'EVALUATED' ? 'default' : 'secondary'} className="bg-blue-100 text-blue-800 hover:bg-blue-100 border-blue-200">
                                Din status: {getUserAssignmentStatusLabel(myUserAssignment.status)}
                            </Badge>
                        )}
                        {canManageSubmissions && (
                            <Button variant="outline"
                                    onClick={() => navigate(`/admin/assignments/${assignmentId}/edit`)}
                                    className="gap-2">
                                <Edit className="h-4 w-4"/>
                                Redigera uppgift
                            </Button>
                        )}
                    </div>
                </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div className="md:col-span-2 space-y-6">
                    <Card>
                        <CardHeader>
                            <CardTitle>Beskrivning</CardTitle>
                        </CardHeader>
                        <CardContent>
                            <p className="whitespace-pre-wrap">{assignment.description || 'Ingen beskrivning tillgänglig.'}</p>
                        </CardContent>
                    </Card>

                    {isStudent && myAssignmentLoading && (
                        <Card>
                            <CardContent className="pt-6">
                                <p className="text-sm text-muted-foreground">Laddar din inlämning...</p>
                            </CardContent>
                        </Card>
                    )}
                    {isStudent && myAssignmentError && (
                        <Card>
                            <CardContent className="pt-6">
                                <p className="text-sm text-destructive">{myAssignmentError}</p>
                            </CardContent>
                        </Card>
                    )}
                    {isStudent && !myAssignmentLoading && !myAssignmentError && !myUserAssignment && (
                        <Card>
                            <CardContent className="pt-6">
                                <p className="text-sm text-muted-foreground italic">
                                    Du har inte blivit tilldelad denna uppgift än.
                                </p>
                            </CardContent>
                        </Card>
                    )}
                    
                    {isStudent && myUserAssignment && (
                        <Card>
                            <CardHeader>
                                <CardTitle className="flex items-center justify-between">
                                    <span>Din inlämning</span>
                                    <Badge variant={myUserAssignment.status === 'EVALUATED' ? 'default' : 'secondary'}>
                                        {getUserAssignmentStatusLabel(myUserAssignment.status)}
                                    </Badge>
                                </CardTitle>
                            </CardHeader>
                            <CardContent className="space-y-4">
                                {myUserAssignment.status === 'EVALUATED' ? (
                                    <div className="space-y-4">
                                        <div className="p-4 bg-muted rounded-lg">
                                            <h4 className="font-bold mb-1">Resultat</h4>
                                            <p className="text-2xl font-bold text-primary">{myUserAssignment.grade}</p>
                                        </div>
                                        <div>
                                            <h4 className="font-bold mb-1">Feedback</h4>
                                            <p className="whitespace-pre-wrap text-muted-foreground">
                                                {myUserAssignment.feedback || 'Ingen feedback lämnad.'}
                                            </p>
                                        </div>
                                        <div className="pt-4 border-t">
                                            <h4 className="font-bold mb-1">Ditt svar</h4>
                                            <p className="whitespace-pre-wrap">{submissionContent}</p>
                                        </div>
                                    </div>
                                ) : (
                                    <div className="space-y-4">
                                        <Textarea 
                                            placeholder="Skriv ditt svar här..."
                                            className="min-h-[200px]"
                                            value={submissionContent}
                                            onChange={e => setSubmissionContent(e.target.value)}
                                            disabled={isSubmitting}
                                        />
                                        <div className="flex justify-end">
                                            <Button 
                                                onClick={handleSubmission} 
                                                disabled={isSubmitting || !submissionContent.trim()}
                                                className="gap-2"
                                            >
                                                {isSubmitting ? 'Lämnar in...' : (
                                                    <>
                                                        <Send className="h-4 w-4"/>
                                                        Lämna in
                                                    </>
                                                )}
                                            </Button>
                                        </div>
                                    </div>
                                )}
                            </CardContent>
                        </Card>
                    )}

                    {(!isStudent || myUserAssignment) && (
                        <CommentSection
                            assignmentId={isStudent ? undefined : assignmentId}
                            userAssignmentId={isStudent ? myUserAssignment?.id : undefined}
                        />
                    )}
                </div>

                <div className="space-y-6">
                    {canManageSubmissions && (
                        <Card>
                            <CardHeader>
                                <CardTitle className="flex items-center gap-2">
                                    <ClipboardCheck className="h-5 w-5"/>
                                    Studentinlämningar
                                </CardTitle>
                            </CardHeader>
                            <CardContent>
                                {loadingSubmissions ? (
                                    <p className="text-sm text-muted-foreground">Laddar inlämningar...</p>
                                ) : userAssignments.length === 0 ? (
                                    <p className="text-sm text-muted-foreground italic">Inga studenter tilldelade
                                        än.</p>
                                ) : (
                                    <Table>
                                        <TableHeader>
                                            <TableRow>
                                                <TableHead>Elev</TableHead>
                                                <TableHead>Status</TableHead>
                                                <TableHead className="text-right">Betyg</TableHead>
                                            </TableRow>
                                        </TableHeader>
                                        <TableBody>
                                            {userAssignments.map((ua) => (
                                                <TableRow
                                                    key={ua.id}
                                                    className="cursor-pointer hover:bg-muted/50"
                                                    onClick={() => navigate(`/assignments/${assignmentId}/grade/${ua.student.id}`)}
                                                >
                                                    <TableCell className="font-medium text-xs truncate max-w-[100px]">
                                                        <Link 
                                                            to={`/profile/${ua.student.id}`} 
                                                            className="hover:underline text-primary"
                                                            onClick={(e) => e.stopPropagation()}
                                                        >
                                                            {ua.student.username}
                                                        </Link>
                                                    </TableCell>
                                                    <TableCell>
                                                        <Badge
                                                            variant={ua.status === 'EVALUATED' ? 'default' : 'secondary'}
                                                            className="text-[10px] px-1 h-5">
                                                            {ua.status === 'EVALUATED' ? 'Klar' : 'Väntar'}
                                                        </Badge>
                                                    </TableCell>
                                                    <TableCell className="text-right font-bold">
                                                        {ua.grade || '-'}
                                                    </TableCell>
                                                </TableRow>
                                            ))}
                                        </TableBody>
                                    </Table>
                                )}
                            </CardContent>
                        </Card>
                    )}

                    <Card>
                        <CardHeader>
                            <CardTitle>Information</CardTitle>
                        </CardHeader>
                        <CardContent className="space-y-4">
                            <div className="flex items-center gap-2 text-sm">
                                <Calendar className="h-4 w-4 text-muted-foreground"/>
                                <span className="font-semibold">Deadline:</span>
                                <span>{formatDate(assignment.deadline)}</span>
                            </div>
                            <div className="flex items-center gap-2 text-sm">
                                <User className="h-4 w-4 text-muted-foreground"/>
                                <span className="font-semibold">Skapad av:</span>
                                <span>
                                    <Link to={`/profile/${assignment.creator?.id}`} className="hover:underline text-primary">
                                        {assignment.creator?.username}
                                    </Link>
                                </span>
                            </div>
                            <div className="flex items-center gap-2 text-sm">
                                <Calendar className="h-4 w-4 text-muted-foreground"/>
                                <span className="font-semibold">Skapad:</span>
                                <span>{formatDate(assignment.createdAt)}</span>
                            </div>
                        </CardContent>
                    </Card>

                    {(!isStudent || myUserAssignment) && (
                        <FileSection
                            files={isStudent ? (myUserAssignment?.files ?? []) : (assignment.files ?? [])}
                            assignmentId={isStudent ? undefined : assignmentId}
                            userAssignmentId={isStudent ? myUserAssignment?.id : undefined}
                        />
                    )}
                </div>
            </div>
        </div>
    );
}