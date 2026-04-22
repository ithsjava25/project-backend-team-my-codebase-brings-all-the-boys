import {useState, useEffect} from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import {courseApi} from '@/api/courses';
import {assignmentApi} from '@/api/assignments';
import {Button} from '@/components/ui/button';
import {Card, CardContent, CardHeader, CardTitle} from '@/components/ui/card';
import {Input} from '@/components/ui/input';
import {Label} from '@/components/ui/label';
import {Textarea} from '@/components/ui/textarea';
import {ArrowLeft} from 'lucide-react';

export default function AssignmentCreatePage() {
    const {courseId} = useParams();
    const navigate = useNavigate();
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [course, setCourse] = useState(null);
    const [loading, setLoading] = useState(true);

    const [form, setForm] = useState({
        title: '',
        description: '',
        deadline: '',
        courseId: courseId || ''
    });

    useEffect(() => {
        if (!courseId) {
            setLoading(false);
            return;
        }

        const fetchCourse = async () => {
            try {
                const data = await courseApi.getCourseById(courseId);
                setCourse(data);
            } catch (err) {
                console.error('Failed to fetch course:', err);
            } finally {
                setLoading(false);
            }
        };

        fetchCourse();
    }, [courseId]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (isSubmitting) return;

        setIsSubmitting(true);
        try {
            await assignmentApi.createAssignment(form);
            alert('Uppgiften har skapats!');
            navigate(`/courses/${courseId}?tab=assignments`);
        } catch (err) {
            console.error('Failed to create assignment:', err);
            alert(err.response?.data?.message || 'Kunde inte skapa uppgift.');
        } finally {
            setIsSubmitting(false);
        }
    };

    if (loading) return <div className="p-8 text-center">Laddar...</div>;

    return (
        <div className="p-8">
            <div className="max-w-2xl mx-auto space-y-6">
                <Button 
                    variant="ghost" 
                    onClick={() => navigate(-1)} 
                    className="gap-2"
                >
                    <ArrowLeft className="h-4 w-4"/>
                    Tillbaka
                </Button>

                <h1 className="text-3xl font-bold">Skapa Ny Uppgift</h1>
                {course && <p className="text-muted-foreground text-lg">Kurs: {course.name}</p>}

                <Card>
                    <CardHeader>
                        <CardTitle>Uppgiftsinformation</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <form onSubmit={handleSubmit} className="space-y-6">
                            <div className="space-y-2">
                                <Label htmlFor="title">Titel</Label>
                                <Input
                                    id="title"
                                    required
                                    placeholder="T.ex. Inlämning 1: Introduktion"
                                    value={form.title}
                                    onChange={e => setForm({...form, title: e.target.value})}
                                />
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="description">Beskrivning</Label>
                                <Textarea
                                    id="description"
                                    placeholder="Beskriv vad studenterna ska göra..."
                                    className="min-h-[150px]"
                                    value={form.description}
                                    onChange={e => setForm({...form, description: e.target.value})}
                                />
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="deadline">Slutdatum (Deadline)</Label>
                                <Input
                                    id="deadline"
                                    type="datetime-local"
                                    value={form.deadline}
                                    onChange={e => setForm({...form, deadline: e.target.value})}
                                />
                            </div>

                            <div className="flex justify-end gap-3 pt-4">
                                <Button 
                                    type="button" 
                                    variant="outline" 
                                    onClick={() => navigate(-1)}
                                >
                                    Avbryt
                                </Button>
                                <Button type="submit" disabled={isSubmitting}>
                                    {isSubmitting ? 'Skapar...' : 'Skapa Uppgift'}
                                </Button>
                            </div>
                        </form>
                    </CardContent>
                </Card>
            </div>
        </div>
    );
}
