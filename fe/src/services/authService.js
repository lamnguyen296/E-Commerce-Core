import axiosClient from '../api/axiosClient';

export const authService = {
  login: async (username, password) => {
    return await axiosClient.post('/auth/token', { username, password });
  },
  getMyInfo: async () => {
    return await axiosClient.get('/users/myInfo');
  },
  logout: async (accessToken) => {
    try {
      return await axiosClient.post('/auth/logout', { accessToken });
    } catch {
      // ignore
    }
  },
};
