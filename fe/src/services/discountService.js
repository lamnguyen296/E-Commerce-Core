import axiosClient from '../api/axiosClient';

export const discountService = {
  getMyDiscounts: async () => {
    return await axiosClient.get('/discounts/my-discounts');
  },
};
