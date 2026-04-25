import {useState, useEffect} from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import {assignmentApi} from '@/api/assignments';
import {Button} from '@/components/ui/button';
import {Card, CardContent, CardHeader, CardTitle} from '@/components/ui/card';
import {Input} from '@/components/ui/input';
import {Label} from '@/components/ui/label';
import {Textarea} from '@/components/ui/textarea';
import {Alert, AlertDescription} from '@/components/ui/alert';
import {ArrowLeft} from 'lucide-react';

export default function AssignmentEditPage() {
    const {id} = useParams();
    const navigate = useNavigate();
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [submitError, setSubmitError] = useState(null);

    const [form, setForm] = useState({
        title: '',
        description: '',
        deadline: '',
        status: ''
    });

    useEffect(() => {
        let cancelled = false;
        const fetchAssignment = async () => {
            try {
                const data = await assignmentApi.getAssignmentById(id);
                if (!cancelled) {
                    setForm({
                        title: data.title || '',
                        description: data.description || '',
                        deadline: data.deadline ? data.deadline.substring(0, 16) : '',
                        status: data.status || 'CREATED'
                    });
                }
            } catch (err) {
                if (!cancelled) {
                    console.error('Failed to fetch assignment:', err);
                    setError('Kunde inte hämta uppgiftsinformation.');
                }
            } finally {
                if (!cancelled) {
                    setLoading(false);
                }
            }
        };

        if (id) fetchAssignment();
        
        return () => {
            cancelled = true;
        };
    }, [id]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (isSubmitting || !id) return;

        setIsSubmitting(true);
        setSubmitError(null);
        try {
            await assignmentApi.updateAssignment(id, form);
            navigate(`/assignments/${id}`);
        } catch (err) {
            console.error('Failed to update assignment:', err);
            setSubmitError(err.response?.data?.message || 'Kunde inte uppdatera uppgift.');
        } finally {
            setIsSubmitting(false);
        }
    };

    if (loading) return <div className="p-8 text-center">Laddar...</div>;
    
    if (error) {
        return (
            <div className="p-8 text-center">
                <p className="text-destructive mb-4">{error}</p>
                <Button onClick={() => navigate(-1)}>Gå tillbaka</Button>
            </div>
        );
    }

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

                <h1 className="text-3xl font-bold">Redigera Uppgift</h1>

                <Card>
                    <CardHeader>
                        <CardTitle>Uppgiftsinformation</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <form onSubmit={handleSubmit} className="space-y-6">
                            {submitError && (
                                <Alert variant="destructive">
                                    <AlertDescription>{submitError}</AlertDescription>
                                </Alert>
                            )}
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
                                    {isSubmitting ? 'Sparar...' : 'Spara ändringar'}
                                </Button>
                            </div>
                        </form>
                    </CardContent>
                </Card>
            </div>
        </div>
    );
}
