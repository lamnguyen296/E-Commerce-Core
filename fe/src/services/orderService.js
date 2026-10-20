import axiosClient from '../api/axiosClient';

export const orderService = {
  createOrder: async (orderData) => {
    return await axiosClient.post('/orders', orderData);
  },
  getOrderById: async (orderId) => {
    return await axiosClient.get(`/orders/${orderId}`);
  },
};
