import client from './client';
import axios from 'axios';

export const fileApi = {
  getUploadUrl: async (fileName, contentType, assignmentId = null, commentId = null) => {
    const response = await client.post('/files/upload-url', {
      fileName,
      contentType,
      assignmentId,
      commentId
    });
    return response.data; // returns { uploadUrl, s3Key }
  },

  finalizeUpload: async (s3Key, fileName, contentType, fileSize, assignmentId = null, commentId = null) => {
    const response = await client.post(`/files/finalize?s3Key=${encodeURIComponent(s3Key)}`, {
      fileName,
      contentType,
      fileSize,
      assignmentId,
      commentId
    });
    return response.data;
  },

  uploadToS3: async (uploadUrl, file) => {
    const isLocal = uploadUrl.startsWith('/api/files/local');

    if (isLocal) {
      return client.put(uploadUrl, file, {
        headers: { 'Content-Type': file.type },
        baseURL: ''  // Important: prevents the client's baseURL from being added
      });
    }

    // S3 presigned URL
    // We use raw axios here because we don't want the default client headers/baseUrl
    return axios.put(uploadUrl, file, {
      headers: { 'Content-Type': file.type }
    });
  },

  getFileMetadata: async (fileId) => {
    const response = await client.get(`/files/${fileId}`);
    return response.data;
  }
};