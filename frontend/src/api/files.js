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
    const response = await client.post(`/files/finalize?s3Key=${s3Key}`, {
      fileName,
      contentType,
      fileSize,
      assignmentId,
      commentId
    });
    return response.data;
  },

  uploadToS3: async (uploadUrl, file) => {
    // We use raw axios here because we don't want the default client headers/baseUrl
    return axios.put(uploadUrl, file, {
      headers: {
        'Content-Type': file.type
      }
    });
  },

  getFileMetadata: async (fileId) => {
    const response = await client.get(`/files/${fileId}`);
    return response.data;
  }
};