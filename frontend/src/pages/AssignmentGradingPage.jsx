import {useState, useEffect} from 'react';
import {useParams, useNavigate} from 'react-router-dom';
import {userAssignmentApi} from '@/api/userAssignments';
import {Card, CardContent, CardHeader, CardTitle} from '@/components/ui/card';
import {Button} from '@/components/ui/button';
import {Textarea} from '@/components/ui/textarea';
import {Label} from '@/components/ui/label';
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select";
import {ArrowLeft, Download, FileText} from 'lucide-react';
import {Badge} from '@/components/ui/badge';
import {CommentSection} from '@/components/dashboard/CommentSection';

export default function AssignmentGradingPage() {
    const {assignmentId, studentId} = useParams();
    const navigate = useNavigate();
    const [ua, setUa] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const [form, setForm] = useState({
        grade: '',
        feedback: ''
    });

    useEffect(() => {
        const fetchData = async () => {
            try {
                setLoading(true);
                const data = await userAssignmentApi.getByAssignmentAndStudent(assignmentId, studentId);
                setUa(data);
                setForm({
                    grade: data.grade || '',
                    feedback: data.feedback || ''
                });
            } catch (err) {
                setError(err.response?.data?.message || 'Kunde inte hämta inlämning.');
            } finally {
                setLoading(false);
            }
        };
        fetchData();
    }, [assignmentId, studentId]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!form.grade) {
            alert('Vänligen välj ett betyg.');
            return;
        }

        setIsSubmitting(true);
        try {
            await userAssignmentApi.evaluate(ua.id, form);
            alert('Bedömning sparad!');
            navigate(-1);
        } catch (err) {
            console.error('Grading failed:', err);
            alert(err.response?.data?.message || 'Kunde inte spara bedömning.');
        } finally {
            setIsSubmitting(false);
        }
    };

    if (loading) return <div className="p-8 text-center">Laddar inlämning...</div>;
    if (error) return <div className="p-8 text-center text-destructive">Fel: {error}</div>;
    if (!ua) return <div className="p-8 text-center">Inlämning hittades inte.</div>;

    const latestSubmission = ua.submissions && ua.submissions.length > 0 
        ? ua.submissions[ua.submissions.length - 1] 
        : null;

    return (
        <div className="p-8 max-w-4xl mx-auto space-y-6">
            <Button variant="ghost" onClick={() => navigate(-1)} className="gap-2">
                <ArrowLeft className="h-4 w-4"/>
                Tillbaka
            </Button>

            <div className="flex justify-between items-start">
                <div>
                    <h1 className="text-3xl font-bold">Bedöm inlämning</h1>
                    <p className="text-muted-foreground">Student: {ua.student.username} ({ua.student.email})</p>
                </div>
                <Badge variant={ua.status === 'EVALUATED' ? 'default' : 'secondary'}>
                    {ua.status === 'EVALUATED' ? 'Bedömd' : 'Väntar på bedömning'}
                </Badge>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                {/* Student's Work */}
                <div className="md:col-span-2 space-y-6">
                    <Card>
                        <CardHeader>
                            <CardTitle>Inlämnat arbete</CardTitle>
                        </CardHeader>
                        <CardContent className="space-y-4">
                            {latestSubmission ? (
                                <>
                                    <div className="prose dark:prose-invert max-w-none">
                                        <p className="whitespace-pre-wrap">{latestSubmission.content}</p>
                                    </div>

                                    {latestSubmission.files && latestSubmission.files.length > 0 && (
                                        <div className="pt-4 border-t">
                                            <h4 className="font-semibold mb-2 flex items-center gap-2">
                                                <FileText className="h-4 w-4"/>
                                                Bifogade filer
                                            </h4>
                                            <div className="grid grid-cols-1 gap-2">
                                                {latestSubmission.files.map(file => (
                                                    <div key={file.id} className="flex items-center justify-between p-2 bg-muted rounded-md">
                                                        <span className="text-sm truncate">{file.fileName}</span>
                                                        <Button size="sm" variant="ghost" asChild>
                                                            <a href={file.downloadUrl} target="_blank" rel="noopener noreferrer">
                                                                <Download className="h-4 w-4"/>
                                                            </a>
                                                        </Button>
                                                    </div>
                                                ))}
                                            </div>
                                        </div>
                                    )}
                                </>
                            ) : (
                                <p className="text-muted-foreground italic">Ingen inlämning har gjorts än.</p>
                            )}
                        </CardContent>
                     </Card>

                     <CommentSection userAssignmentId={ua.id} />
                 </div>

                 {/* Grading Form */}
                <div className="space-y-6">
                    <Card>
                        <CardHeader>
                            <CardTitle>Bedömning</CardTitle>
                        </CardHeader>
                        <CardContent>
                            <form onSubmit={handleSubmit} className="space-y-4">
                                <div className="space-y-2">
                                    <Label htmlFor="grade">Betyg (A-F)</Label>
                                    <Select 
                                        value={form.grade} 
                                        onValueChange={val => setForm({...form, grade: val})}
                                    >
                                        <SelectTrigger id="grade">
                                            <SelectValue placeholder="Välj betyg..." />
                                        </SelectTrigger>
                                        <SelectContent>
                                            <SelectItem value="A">A - Utmärkt</SelectItem>
                                            <SelectItem value="B">B - Mycket bra</SelectItem>
                                            <SelectItem value="C">C - Bra</SelectItem>
                                            <SelectItem value="D">D - Tillfredsställande</SelectItem>
                                            <SelectItem value="E">E - Godkänt</SelectItem>
                                            <SelectItem value="F">F - Icke godkänt</SelectItem>
                                        </SelectContent>
                                    </Select>
                                </div>

                                <div className="space-y-2">
                                    <Label htmlFor="feedback">Feedback</Label>
                                    <Textarea
                                        id="feedback"
                                        placeholder="Skriv din feedback här..."
                                        className="min-h-[150px]"
                                        value={form.feedback}
                                        onChange={e => setForm({...form, feedback: e.target.value})}
                                    />
                                </div>

                                <Button type="submit" className="w-full" disabled={isSubmitting}>
                                    {isSubmitting ? 'Sparar...' : 'Spara bedömning'}
                                </Button>
                            </form>
                        </CardContent>
                    </Card>
                </div>
            </div>
        </div>
    );
}
