import axios from 'axios';
import { API_BASE_URL } from '../config/constants';

const axiosClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

// Request interceptor: add auth token if present
axiosClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor: unwrap BaseResponse
axiosClient.interceptors.response.use(
  (response) => {
    // If backend returns BaseResponse { meta, data }, unwrap data
    if (response.data && response.data.meta !== undefined && response.data.data !== undefined) {
      return response.data.data;
    }
    return response.data;
  },
  (error) => {
    const customError = error.response?.data?.meta || error.response?.data || error.message;
    return Promise.reject(customError);
  }
);

export default axiosClient;
