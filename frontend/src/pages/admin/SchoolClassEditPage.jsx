import {useState, useEffect} from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import {schoolClassApi} from '@/api/schoolClasses';
import {Button} from '@/components/ui/button';
import {Card, CardContent, CardHeader, CardTitle} from '@/components/ui/card';
import {Input} from '@/components/ui/input';
import {Label} from '@/components/ui/label';
import {Textarea} from '@/components/ui/textarea';

export default function SchoolClassEditPage() {
    const {id} = useParams();
    const navigate = useNavigate();
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const [form, setForm] = useState({
        name: '',
        description: ''
    });

    useEffect(() => {
        const fetchClass = async () => {
            try {
                setLoading(true);
                const data = await schoolClassApi.getSchoolClassById(id);
                setForm({
                    name: data.name || '',
                    description: data.description || ''
                });
            } catch (err) {
                console.error('Failed to fetch class:', err);
                setError('Kunde inte hämta klassen.');
            } finally {
                setLoading(false);
            }
        };

        if (id) fetchClass();
    }, [id]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (isSubmitting) return;
        setIsSubmitting(true);
        setError(null);
        try {
            await schoolClassApi.updateSchoolClass(id, form);
            alert('Klassen har uppdaterats!');
            navigate('/admin/school-classes');
        } catch (err) {
            console.error('Failed to update class:', err);
            setError(err.response?.data?.message || 'Kunde inte uppdatera klass.');
        } finally {
            setIsSubmitting(false);
        }
    };

    if (loading) return <div className="p-8">Laddar...</div>;

    return (
        <div className="p-8">
            <h1 className="text-3xl font-bold mb-6">Redigera Klass</h1>
            <Card>
                <CardHeader>
                    <CardTitle>Klassinformation</CardTitle>
                </CardHeader>
                <CardContent>
                    <form onSubmit={handleSubmit} className="space-y-4">
                        <div>
                            <Label htmlFor="class-name">Klassnamn</Label>
                            <Input
                                id="class-name"
                                name="name"
                                required
                                placeholder="T.ex. TE21A"
                                value={form.name}
                                onChange={e => setForm({...form, name: e.target.value})}
                            />
                        </div>

                        <div>
                            <Label htmlFor="class-description">Beskrivning</Label>
                            <Textarea
                                id="class-description"
                                name="description"
                                placeholder="Klassens beskrivning..."
                                value={form.description}
                                onChange={e => setForm({...form, description: e.target.value})}
                            />
                        </div>

                        {error && <p className="text-destructive">Fel: {error}</p>}

                        <div className="flex justify-end gap-2">
                            <Button type="submit" disabled={isSubmitting}>
                                {isSubmitting ? 'Sparar...' : 'Spara ändringar'}
                            </Button>
                            <Button type="button" variant="outline" onClick={() => navigate('/admin/school-classes')}>
                                Avbryt
                            </Button>
                        </div>
                    </form>
                </CardContent>
            </Card>
        </div>
    );
}
