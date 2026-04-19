import {useState, useEffect} from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import {userApi} from '@/api/users';
import {Button} from '@/components/ui/button';
import {Card, CardContent, CardHeader, CardTitle} from '@/components/ui/card';

export default function UserEditPage() {
    const {id} = useParams();
    const navigate = useNavigate();
    const [isSaving, setIsSaving] = useState(false);
    const [form, setForm] = useState(null);

    useEffect(() => {
        let cancelled = false;
        userApi.getUserById(id)
          .then(data => { if (!cancelled) setForm(data); })
          .catch(err => {
              if (!cancelled)
                  alert(err.response?.data?.message || 'Kunde inte hämta användare.');
          });
        return () => { cancelled = true; };
    }, [id]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsSaving(true);
        try {
            await userApi.updateUser(id, form);
            navigate('/admin/users');
        } catch (err) {
            alert(err.response?.data?.message || 'Kunde inte uppdatera användare.');
        } finally {
            setIsSaving(false);
        }
    };

    if (!form) return <p>Laddar...</p>;

    return (
        <Card>
            <CardHeader>
                <CardTitle>Redigera användare</CardTitle>
            </CardHeader>
            <CardContent>
                <form onSubmit={handleSubmit} className="space-y-4">

                    <div>
                        <label htmlFor="username" className="block text-sm font-medium mb-1">
                            Användarnamn
                        </label>
                        <input
                            id="username"
                            name="username"
                            type="text"
                            value={form.username}
                            onChange={e => setForm({...form, username: e.target.value})}
                            className="w-full px-3 py-2 border rounded-md"
                            placeholder="Användarnamn"
                        />
                    </div>

                    <div>
                        <label htmlFor="email" className="block text-sm font-medium mb-1">
                            E-post
                        </label>
                        <input
                            id="email"
                            name="email"
                            type="email"
                            value={form.email}
                            onChange={e => setForm({...form, email: e.target.value})}
                            className="w-full px-3 py-2 border rounded-md"
                            placeholder="E-postadress"
                        />
                    </div>

                    <Button type="submit" disabled={isSaving}>
                        {isSaving ? 'Sparar...' : 'Spara'}
                    </Button>
                </form>
            </CardContent>
        </Card>
    );
}