import {useParams, Link, useNavigate} from 'react-router-dom';
import {useAssignmentDetail} from '@/hooks/useAssignmentDetail';
import {CommentSection} from '@/components/dashboard/CommentSection';
import {FileSection} from '@/components/dashboard/FileSection';
import {Card, CardContent, CardHeader, CardTitle} from '@/components/ui/card';
import {Badge} from '@/components/ui/badge';
import {ArrowLeft, Calendar, User, Edit} from 'lucide-react';
import {Button} from '@/components/ui/button';
import {useAuthContext} from '@/context/AuthContext';

export default function AssignmentDetailPage() {
    const {assignmentId} = useParams();
    const {assignment, loading, error} = useAssignmentDetail(assignmentId);
    const {user} = useAuthContext();
    const navigate = useNavigate();

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
                        {user?.role?.name === 'ROLE_ADMIN' && (
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

                    <CommentSection assignmentId={assignmentId}/>
                </div>

                <div className="space-y-6">
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
                                <span>{assignment.creator?.username}</span>
                            </div>
                            <div className="flex items-center gap-2 text-sm">
                                <Calendar className="h-4 w-4 text-muted-foreground"/>
                                <span className="font-semibold">Skapad:</span>
                                <span>{formatDate(assignment.createdAt)}</span>
                            </div>
                        </CardContent>
                    </Card>

                    <FileSection
                        files={assignment.files ?? []}
                        assignmentId={assignmentId}
                    />
                </div>
            </div>
        </div>
    );
}