import React, { createContext, useContext, useState, useEffect } from 'react';
import { cartService } from '../services/cartService';
import { Toast } from '../components/Toast';

const CartContext = createContext();

export const CartProvider = ({ children }) => {
  const [cart, setCart] = useState({
    items: [],
    totalQuantity: 0,
    totalPrice: 0,
  });
  const [loading, setLoading] = useState(false);
  const [toast, setToast] = useState({ show: false, message: '', type: 'success' });

  const showToast = (message, type = 'success') => {
    setToast({ show: true, message, type });
    setTimeout(() => {
      setToast({ show: false, message: '', type: 'success' });
    }, 3000);
  };

  const closeToast = () => {
    setToast({ show: false, message: '', type: 'success' });
  };

  const fetchCart = async () => {
    try {
      setLoading(true);
      const data = await cartService.getCart();
      if (data) {
        setCart(data);
      }
    } catch (err) {
      console.warn('Backend cart not reachable or empty, using local cart session:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCart();
  }, []);

  const addToCart = async (productVariantId, quantity = 1, productFallback = null) => {
    try {
      const updatedCart = await cartService.addToCart(productVariantId, quantity);
      if (updatedCart) {
        setCart(updatedCart);
        showToast('Added product to cart successfully!', 'success');
        return true;
      }
    } catch (err) {
      console.warn('API add to cart failed, checking fallback...', err);

      // Graceful fallback for offline / preview mode:
      if (productFallback) {
        setCart((prevCart) => {
          const existingItemIndex = prevCart.items.findIndex(
            (item) => item.productVariantId === productVariantId
          );
          let newItems = [...prevCart.items];
          if (existingItemIndex > -1) {
            newItems[existingItemIndex] = {
              ...newItems[existingItemIndex],
              quantity: newItems[existingItemIndex].quantity + quantity,
              subtotal: (newItems[existingItemIndex].quantity + quantity) * (productFallback.price || 0),
            };
          } else {
            newItems.push({
              id: 'local-' + Date.now(),
              productVariantId,
              productId: productFallback.id,
              productName: productFallback.name,
              brand: productFallback.brand,
              imageUrl: productFallback.imageUrl,
              price: productFallback.price,
              quantity,
              subtotal: quantity * (productFallback.price || 0),
            });
          }

          const totalQuantity = newItems.reduce((sum, item) => sum + item.quantity, 0);
          const totalPrice = newItems.reduce((sum, item) => sum + (item.subtotal || 0), 0);

          return {
            items: newItems,
            totalQuantity,
            totalPrice,
          };
        });
        showToast('Added product to cart successfully!', 'success');
        return true;
      } else {
        const errorMsg = typeof err === 'string' ? err : (err?.message || 'Unable to add product to cart!');
        showToast(errorMsg, 'error');
        return false;
      }
    }
  };

  const updateQuantity = async (itemId, quantity) => {
    if (quantity <= 0) {
      return removeItem(itemId);
    }
    // Optimistic UI update
    setCart((prev) => {
      const updatedItems = prev.items.map((it) => {
        if (it.id === itemId) {
          const subtotal = (it.price || 0) * quantity;
          return { ...it, quantity, subtotal };
        }
        return it;
      });
      const totalQuantity = updatedItems.reduce((sum, it) => sum + it.quantity, 0);
      const totalPrice = updatedItems.reduce((sum, it) => sum + (it.subtotal || 0), 0);
      return { ...prev, items: updatedItems, totalQuantity, totalPrice };
    });

    try {
      const updatedCart = await cartService.updateQuantity(itemId, quantity);
      if (updatedCart) {
        setCart(updatedCart);
      }
      return true;
    } catch (err) {
      console.warn('Failed to update quantity via API, reverting...', err);
      fetchCart();
      const msg = typeof err === 'string' ? err : err?.message || 'Failed to update item quantity';
      showToast(msg, 'error');
      return false;
    }
  };

  const removeItem = async (itemId) => {
    // Optimistic UI update
    setCart((prev) => {
      const updatedItems = prev.items.filter((it) => it.id !== itemId);
      const totalQuantity = updatedItems.reduce((sum, it) => sum + it.quantity, 0);
      const totalPrice = updatedItems.reduce((sum, it) => sum + (it.subtotal || 0), 0);
      return { ...prev, items: updatedItems, totalQuantity, totalPrice };
    });

    try {
      const updatedCart = await cartService.removeItem(itemId);
      if (updatedCart) {
        setCart(updatedCart);
      }
      showToast('Item removed from cart', 'success');
      return true;
    } catch (err) {
      console.warn('Failed to remove item via API, reverting...', err);
      fetchCart();
      const msg = typeof err === 'string' ? err : err?.message || 'Failed to remove item';
      showToast(msg, 'error');
      return false;
    }
  };

  const clearCart = async () => {
    setCart({ items: [], totalQuantity: 0, totalPrice: 0 });
    try {
      await cartService.clearCart();
      showToast('Shopping bag cleared', 'success');
      return true;
    } catch (err) {
      console.warn('Failed to clear cart via API, reverting...', err);
      fetchCart();
      return false;
    }
  };

  const applyDiscount = async (discountCode) => {
    try {
      setLoading(true);
      const updatedCart = await cartService.applyDiscount(discountCode);
      if (updatedCart) {
        setCart(updatedCart);
        showToast('Discount applied successfully!', 'success');
        return true;
      }
    } catch (err) {
      const msg = typeof err === 'string' ? err : err?.message || 'Failed to apply discount code';
      showToast(msg, 'error');
      return false;
    } finally {
      setLoading(false);
    }
  };

  const removeDiscount = async () => {
    try {
      setLoading(true);
      const updatedCart = await cartService.removeDiscount();
      if (updatedCart) {
        setCart(updatedCart);
        showToast('Discount removed', 'success');
        return true;
      }
    } catch (err) {
      const msg = typeof err === 'string' ? err : err?.message || 'Failed to remove discount';
      showToast(msg, 'error');
      return false;
    } finally {
      setLoading(false);
    }
  };

  return (
    <CartContext.Provider
      value={{
        cart,
        cartCount: cart.totalQuantity || cart.items?.reduce((sum, i) => sum + i.quantity, 0) || 0,
        loading,
        addToCart,
        updateQuantity,
        removeItem,
        clearCart,
        applyDiscount,
        removeDiscount,
        fetchCart,
        showToast,
      }}
    >
      {children}
      <Toast toast={toast} onClose={closeToast} />
    </CartContext.Provider>
  );
};

export const useCart = () => {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error('useCart must be used within a CartProvider');
  }
  return context;
};
