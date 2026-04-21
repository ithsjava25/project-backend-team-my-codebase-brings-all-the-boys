import {useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {courseApi} from '@/api/courses';
import {Button} from '@/components/ui/button';
import {Card, CardContent, CardHeader, CardTitle} from '@/components/ui/card';

export default function CourseCreatePage() {
    const navigate = useNavigate();
    const [isSubmitting, setIsSubmitting] = useState(false);

    const [form, setForm] = useState({
        name: '',
        description: ''
    });

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (isSubmitting) return;
        setIsSubmitting(true);
        try {
            await courseApi.createCourse(form);
            navigate('/admin/courses');
        } catch (err) {
            alert(err.response?.data?.message || 'Kunde inte skapa kurs.');
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <Card>
            <CardHeader>
                <CardTitle>Skapa kurs</CardTitle>
            </CardHeader>
            <CardContent>
                <form onSubmit={handleSubmit} className="space-y-4">

                    <label htmlFor="course-name" className="sr-only">Kursnamn</label>
                    <input
                        id="course-name"
                        name="name"
                        required
                        placeholder="Kursnamn"
                        value={form.name}
                        onChange={e => setForm({...form, name: e.target.value})}
                    />

                    <label htmlFor="course-description" className="sr-only">Beskrivning</label>
                    <textarea
                        id="course-description"
                        name="description"
                        placeholder="Beskrivning"
                        value={form.description}
                        onChange={e => setForm({...form, description: e.target.value})}
                    />

                    <Button type="submit" disabled={isSubmitting}>
                        {isSubmitting ? 'Skapar...' : 'Skapa'}
                    </Button>
                </form>
            </CardContent>
        </Card>
    );
}