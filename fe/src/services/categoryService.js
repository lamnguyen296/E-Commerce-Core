import axiosClient from '../api/axiosClient';

export const categoryService = {
  getAllCategories: async () => {
    return await axiosClient.get('/categories');
  },
  getFeaturedCategories: async () => {
    return await axiosClient.get('/categories/featured');
  },
  getCategoryBySlug: async (slug) => {
    return await axiosClient.get(`/categories/${slug}`);
  },
};
