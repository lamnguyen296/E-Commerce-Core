import axiosClient from '../api/axiosClient';

export const productService = {
  getAllProducts: async () => {
    return await axiosClient.get('/products');
  },
  getFeaturedProducts: async () => {
    return await axiosClient.get('/products/featured');
  },
  getProductById: async (id) => {
    return await axiosClient.get(`/products/${id}`);
  },
};
