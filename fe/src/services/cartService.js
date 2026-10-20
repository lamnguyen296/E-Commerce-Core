import axiosClient from '../api/axiosClient';

export const cartService = {
  getCart: async () => {
    return await axiosClient.get('/cart');
  },
  addToCart: async (productVariantId, quantity = 1) => {
    return await axiosClient.post('/cart/items', {
      productVariantId,
      quantity,
    });
  },
  updateQuantity: async (itemId, quantity) => {
    return await axiosClient.put(`/cart/items/${itemId}?quantity=${quantity}`);
  },
  removeItem: async (itemId) => {
    return await axiosClient.delete(`/cart/items/${itemId}`);
  },
  clearCart: async () => {
    return await axiosClient.delete('/cart/clear');
  },
  applyDiscount: async (discountCode) => {
    return await axiosClient.post('/cart/discount', { discountCode });
  },
  removeDiscount: async () => {
    return await axiosClient.delete('/cart/discount');
  },
  validateStock: async () => {
    return await axiosClient.get('/cart/validate-stock');
  },
};
