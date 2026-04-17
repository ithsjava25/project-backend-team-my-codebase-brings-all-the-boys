import {useState, useEffect} from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import {userApi} from '@/api/users';
import {Button} from '@/components/ui/button';
import {Card, CardContent, CardHeader, CardTitle} from '@/components/ui/card';

export default function UserEditPage() {
    const {id} = useParams();
    const navigate = useNavigate();

    const [form, setForm] = useState(null);

    useEffect(() => {
        userApi.getUserById(id).then(setForm);
    }, [id]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        await userApi.updateUser(id, form);
        navigate('/admin/users');
    };

    if (!form) return <p>Laddar...</p>;

    return (
        <Card>
            <CardHeader>
                <CardTitle>Redigera användare</CardTitle>
            </CardHeader>
            <CardContent>
                <form onSubmit={handleSubmit} className="space-y-4">

                    <input value={form.username}
                           onChange={e => setForm({...form, username: e.target.value})}/>

                    <input value={form.email}
                           onChange={e => setForm({...form, email: e.target.value})}/>

                    <Button type="submit">Spara</Button>
                </form>
            </CardContent>
        </Card>
    );
}