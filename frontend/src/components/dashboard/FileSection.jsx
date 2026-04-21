import { useState } from 'react';
import { fileApi } from '@/api/files';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Dialog, DialogContent } from '@/components/ui/dialog';
import { FileIcon, Upload, Download, Loader2, X, ZoomIn } from 'lucide-react';

const IMAGE_TYPES = ['image/png', 'image/jpeg', 'image/gif', 'image/webp', 'image/svg+xml'];

export function FileSection({ files: initialFiles = [], assignmentId, commentId }) {
  const [files, setFiles] = useState(initialFiles);
  const [isUploading, setIsUploading] = useState(false);
  const [uploadError, setUploadError] = useState(null);
  const [previewFile, setPreviewFile] = useState(null);

  const handleUpload = async (e) => {
    const selectedFile = e.target.files[0];
    if (!selectedFile) return;

    try {
      setIsUploading(true);
      setUploadError(null);

      const { uploadUrl, s3Key } = await fileApi.getUploadUrl(
        selectedFile.name,
        selectedFile.type,
        assignmentId,
        commentId
      );

      await fileApi.uploadToS3(uploadUrl, selectedFile);

      const savedFile = await fileApi.finalizeUpload(
        s3Key,
        selectedFile.name,
        selectedFile.type,
        selectedFile.size,
        assignmentId,
        commentId
      );

      setFiles((prev) => [...prev, savedFile]);
    } catch (error) {
      console.error('Upload failed:', error);
      setUploadError('Uppladdningen misslyckades. Försök igen.');
    } finally {
      setIsUploading(false);
      e.target.value = '';
    }
  };

  const handleDownload = async (file) => {
    try {
      const response = await fetch(file.downloadUrl, {
        credentials: 'include'
      });
      const blob = await response.blob();
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = file.fileName;
      a.click();
      URL.revokeObjectURL(url);
    } catch (error) {
      console.error('Download failed:', error);
    }
  };

  const isImage = (file) => IMAGE_TYPES.includes(file.contentType);

  const formatFileSize = (bytes) => {
    if (!bytes || bytes <= 0) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.min(Math.floor(Math.log(bytes) / Math.log(k)), sizes.length - 1);
    return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i];
  };

  return (
    <>
      <Card>
        <CardHeader className="flex flex-row items-center justify-between space-y-0">
          <CardTitle>Bilagor ({files.length})</CardTitle>
          <div className="relative">
            <input
              type="file"
              id="file-upload"
              className="hidden"
              onChange={handleUpload}
              disabled={isUploading}
            />
            <Button variant="outline" size="sm" asChild disabled={isUploading}>
              <label htmlFor="file-upload" className="cursor-pointer">
                {isUploading ? (
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                ) : (
                  <Upload className="mr-2 h-4 w-4" />
                )}
                Ladda upp
              </label>
            </Button>
          </div>
        </CardHeader>
        <CardContent className="space-y-4">
          {uploadError && (
            <div className="text-sm text-destructive flex items-center justify-between bg-destructive/10 p-2 rounded">
              {uploadError}
              <Button variant="ghost" size="icon" className="h-4 w-4" onClick={() => setUploadError(null)}>
                <X className="h-3 w-3" />
              </Button>
            </div>
          )}

          {files.length === 0 ? (
            <p className="text-sm text-muted-foreground">Inga bilagor uppladdade.</p>
          ) : (
            <div className="space-y-2">
              {files.map((file) => (
                <div
                  key={file.id}
                  className="flex items-center justify-between p-2 rounded-md border bg-muted/30"
                >
                  <div className="flex items-center gap-3 overflow-hidden">
                    <FileIcon className="h-4 w-4 text-blue-500 shrink-0" />
                    <div className="flex flex-col overflow-hidden">
                      <span className="text-sm font-medium truncate">{file.fileName}</span>
                      <span className="text-xs text-muted-foreground">{formatFileSize(file.fileSize)}</span>
                    </div>
                  </div>
                  <div className="flex items-center gap-1">
                    {isImage(file) && (
                      <Button variant="ghost" size="icon" onClick={() => setPreviewFile(file)}>
                        <ZoomIn className="h-4 w-4" />
                      </Button>
                    )}
                    <Button variant="ghost" size="icon" onClick={() => handleDownload(file)}>
                      <Download className="h-4 w-4" />
                    </Button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </CardContent>
      </Card>

      <Dialog open={!!previewFile} onOpenChange={() => setPreviewFile(null)}>
        <DialogContent className="max-w-[95vw] w-fit p-4 sm:p-6 max-w-none!">
          <img
            src={previewFile?.downloadUrl}
            alt={previewFile?.fileName}
            className="max-w-[90vw] max-h-[80vh] object-contain rounded"
          />
          <p className="text-center text-sm text-muted-foreground mt-2">
            {previewFile?.fileName}
          </p>
        </DialogContent>
      </Dialog>
    </>
  );
}