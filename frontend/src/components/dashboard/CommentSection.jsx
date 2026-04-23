import {useState, useEffect} from 'react';
import {useAuthContext} from '@/context/AuthContext';
import {commentApi} from '@/api/comments';
import {Card, CardContent, CardHeader, CardTitle} from '@/components/ui/card';
import {Button} from '@/components/ui/button';
import {Textarea} from '@/components/ui/textarea';
import {Avatar, AvatarFallback} from '@/components/ui/avatar';
import {formatDistanceToNow} from 'date-fns';
import {sv} from 'date-fns/locale';
import {Paperclip, Download} from 'lucide-react';

export function CommentSection({assignmentId, userAssignmentId}) {
    const [comments, setComments] = useState([]);
    const [newComment, setNewComment] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const {user} = useAuthContext();

    const fetchComments = async () => {
        try {
            setIsLoading(true);
            let data;
            if (userAssignmentId) {
                data = await commentApi.getPersonalComments(userAssignmentId);
            } else if (assignmentId) {
                data = await commentApi.getCommentsByAssignment(assignmentId);
            }
            setComments(data || []);
        } catch (error) {
            console.error('Failed to fetch comments:', error);
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        if (userAssignmentId || assignmentId) {
            fetchComments();
        }
    }, [userAssignmentId, assignmentId]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!newComment.trim()) return;

        try {
            setIsSubmitting(true);
            let comment;
            if (userAssignmentId) {
                comment = await commentApi.addPersonalComment(userAssignmentId, newComment);
            } else if (assignmentId) {
                comment = await commentApi.addComment(assignmentId, newComment);
            }
            setComments([...comments, comment]);
            setNewComment('');
        } catch (error) {
            console.error('Failed to add comment:', error);
        } finally {
            setIsSubmitting(false);
        }
    };

    const getInitials = (username) => {
        if (!username) return '?';
        return username.substring(0, 2).toUpperCase();
    };

    return (
        <Card className="mt-6">
            <CardHeader>
                <CardTitle>Kommentarer ({comments.length})</CardTitle>
            </CardHeader>
            <CardContent className="space-y-6">
                {/* Comment List */}
                <div className="space-y-4">
                    {isLoading ? (
                        <p className="text-sm text-muted-foreground">Laddar kommentarer...</p>
                    ) : comments.length === 0 ? (
                        <p className="text-sm text-muted-foreground">Inga kommentarer än.</p>
                    ) : (
                        comments.filter((c) => c?.id).map((comment) => (
                            <div key={comment.id} className="flex gap-4">
                                <Avatar className="h-8 w-8">
                                    <AvatarFallback>{getInitials(comment.author?.username)}</AvatarFallback>
                                </Avatar>
                                <div className="flex-1 space-y-1">
                                    <div className="flex items-center gap-2">
                                        <span className="text-sm font-semibold">{comment.author?.username}</span>
                                        <span className="text-xs text-muted-foreground">
                      {comment.createdAt ? formatDistanceToNow(new Date(comment.createdAt), {
                          addSuffix: true,
                          locale: sv
                      }) : 'Just nu'}
                    </span>
                                    </div>
                                    <p className="text-sm">{comment.text}</p>
                                    {comment.files && comment.files.length > 0 && (
                                        <div className="mt-2 space-y-1">
                                            {comment.files.map((file) => (
                                                <a
                                                    key={file.id}
                                                    href={file.downloadUrl}
                                                    target="_blank"
                                                    rel="noopener noreferrer"
                                                    className="flex items-center gap-2 text-sm text-blue-600 hover:text-blue-800"
                                                >
                                                    <Paperclip className="h-3 w-3"/>
                                                    <span>{file.fileName}</span>
                                                    <Download className="h-3 w-3"/>
                                                </a>
                                            ))}
                                        </div>
                                    )}
                                </div>
                            </div>
                        ))
                    )}
                </div>

                {/* Add Comment Form */}
                {user ? (
                    <form onSubmit={handleSubmit} className="space-y-4 pt-4 border-t">
                        <div className="flex gap-4">
                            <Avatar className="h-8 w-8">
                                <AvatarFallback>{getInitials(user?.username)}</AvatarFallback>
                            </Avatar>
                            <div className="flex-1 space-y-2">
                                <Textarea
                                    placeholder="Skriv en kommentar..."
                                    value={newComment}
                                    onChange={(e) => setNewComment(e.target.value)}
                                    className="min-h-[100px]"
                                />
                                <div className="flex justify-end">
                                    <Button type="submit" disabled={isSubmitting || !newComment.trim()}>
                                        {isSubmitting ? 'Skickar...' : 'Skicka kommentar'}
                                    </Button>
                                </div>
                            </div>
                        </div>
                    </form>) : (
                    <p className="text-sm text-muted-foreground pt-4 border-t">
                        Logga in för att kommentera.
                    </p>
                )}
            </CardContent>
        </Card>
    );
}