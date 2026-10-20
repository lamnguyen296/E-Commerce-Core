import React, { useState, useMemo, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { 
  Plus, 
  Minus, 
  ArrowRight, 
  ShieldCheck, 
  RefreshCw, 
  Bell,
  Settings,
  ShoppingBag,
  ArrowLeft,
  X,
  Tag,
  Ticket,
  CheckCircle2,
  Truck
} from 'lucide-react';
import { useCart } from '../context/CartContext';
import { cartService } from '../services/cartService';
import { discountService } from '../services/discountService';
import { orderService } from '../services/orderService';
import { formatCurrency } from '../utils/formatters';

export const CartPage = () => {
  const { 
    cart, 
    updateQuantity, 
    removeItem, 
    clearCart, 
    applyDiscount, 
    removeDiscount, 
    fetchCart, 
    showToast, 
    loading 
  } = useCart();
  
  const [isValidating, setIsValidating] = useState(false);
  const [voucherCode, setVoucherCode] = useState('');
  const [isApplyingVoucher, setIsApplyingVoucher] = useState(false);
  const [isVoucherModalOpen, setIsVoucherModalOpen] = useState(false);
  const [availableVouchers, setAvailableVouchers] = useState([]);
  const [loadingVouchers, setLoadingVouchers] = useState(false);

  // Checkout modal & state
  const [isCheckoutModalOpen, setIsCheckoutModalOpen] = useState(false);
  const [isSubmittingOrder, setIsSubmittingOrder] = useState(false);
  const [orderSuccess, setOrderSuccess] = useState(null);

  const [shippingForm, setShippingForm] = useState({
    shippingName: '',
    shippingPhone: '',
    shippingProvince: 'Hồ Chí Minh',
    shippingDistrict: 'Quận 1',
    shippingWard: 'Phường Bến Nghé',
    shippingStreet: '',
    paymentMethod: 'COD',
    note: '',
  });

  // Tự động fetch lại giỏ hàng từ Database khi vào trang
  useEffect(() => {
    fetchCart();
  }, []);

  const items = cart?.items || [];
  const isEmpty = items.length === 0;

  // Kiểm tra xem có sản phẩm nào bị hết hàng hoặc đạt trần tồn kho không
  const hasOutOfStock = useMemo(() => {
    return items.some((it) => {
      const stock = it.stockQuantity !== undefined ? it.stockQuantity : 999;
      return stock <= 0 || it.quantity > stock;
    });
  }, [items]);

  // Subtotal (tổng tiền hàng gốc từ DB)
  const rawSubtotal = cart?.totalPrice || items.reduce((sum, it) => sum + (it.subtotal || it.price * it.quantity), 0);
  
  // Discount từ Backend (nếu đã áp mã)
  const discountAmount = cart?.discountAmount || 0;
  const appliedDiscount = cart?.appliedDiscount || null;

  // Phí vận chuyển Expedited Shipping (145,000 ₫ hoặc miễn phí nếu giỏ trống)
  const shippingFee = rawSubtotal > 0 ? 145000 : 0;
  
  // Tổng thanh toán cuối cùng (tính dựa trên dữ liệu backend)
  const totalAmount = rawSubtotal > 0
    ? Math.max(0, (cart?.grandTotal !== undefined ? cart.grandTotal : (rawSubtotal - discountAmount)) + shippingFee)
    : 0;

  // Xử lý tăng/giảm số lượng
  const handleUpdateQuantity = async (item, newQty) => {
    if (newQty <= 0) {
      handleRemoveItem(item.id);
      return;
    }
    const stock = item.stockQuantity !== undefined ? item.stockQuantity : 999;
    if (newQty > stock) {
      showToast(`Cannot increase: Only ${stock} items available in stock!`, 'error');
      return;
    }
    await updateQuantity(item.id, newQty);
  };

  // Xóa sản phẩm khỏi DB
  const handleRemoveItem = async (itemId) => {
    await removeItem(itemId);
  };

  // Re-validate Inventory: gọi API Backend để kiểm tra số lượng tồn kho mới nhất trong MySQL
  const handleRevalidate = async () => {
    try {
      setIsValidating(true);
      const res = await cartService.validateStock();
      await fetchCart();
      if (res && res.isValid) {
        showToast('Inventory validated! All stock levels are synchronized.', 'success');
      } else {
        showToast('Some items in your cart exceed available stock. Please adjust quantities.', 'error');
      }
    } catch (err) {
      console.warn('Validate stock error:', err);
      showToast('Failed to validate stock with database.', 'error');
    } finally {
      setIsValidating(false);
    }
  };

  // Xử lý áp dụng mã giảm giá
  const handleApplyVoucher = async (codeToApply) => {
    const code = (codeToApply || voucherCode).trim();
    if (!code) {
      showToast('Please enter a voucher code', 'error');
      return;
    }
    setIsApplyingVoucher(true);
    const success = await applyDiscount(code);
    setIsApplyingVoucher(false);
    if (success) {
      setVoucherCode('');
      setIsVoucherModalOpen(false);
    }
  };

  // Mở modal chọn voucher
  const handleOpenVoucherModal = async () => {
    setIsVoucherModalOpen(true);
    try {
      setLoadingVouchers(true);
      const vouchers = await discountService.getMyDiscounts();
      setAvailableVouchers(vouchers || []);
    } catch (err) {
      console.warn('Failed to load discounts:', err);
    } finally {
      setLoadingVouchers(false);
    }
  };

  // Mở modal thanh toán
  const handleOpenCheckout = () => {
    if (hasOutOfStock) {
      showToast('Cannot proceed: Some items in your cart are out of stock. Please adjust quantities.', 'error');
      return;
    }
    if (isEmpty) {
      showToast('Your cart is empty', 'error');
      return;
    }
    setIsCheckoutModalOpen(true);
  };

  // Submit đặt hàng
  const handleSubmitOrder = async (e) => {
    e.preventDefault();
    if (!shippingForm.shippingName || !shippingForm.shippingPhone || !shippingForm.shippingStreet) {
      showToast('Please fill in all required shipping details', 'error');
      return;
    }

    try {
      setIsSubmittingOrder(true);
      const createdOrder = await orderService.createOrder(shippingForm);
      if (createdOrder) {
        setIsCheckoutModalOpen(false);
        setOrderSuccess(createdOrder);
        await fetchCart();
        showToast('Order placed successfully!', 'success');
      }
    } catch (err) {
      const msg = typeof err === 'string' ? err : err?.message || 'Failed to place order';
      showToast(msg, 'error');
    } finally {
      setIsSubmittingOrder(false);
    }
  };

  return (
    <div className="min-h-screen bg-[#f8fafc] text-slate-800 pb-20 selection:bg-blue-100 selection:text-blue-900">
      
      {/* Top Header Section */}
      <div className="max-w-[1240px] mx-auto px-4 sm:px-6 lg:px-8 pt-10 pb-8">
        
        {/* Navigation & Header */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-5 mb-5 border-b border-slate-200/60">
          <div>
            <h1 className="text-3xl sm:text-[34px] font-extrabold text-[#0f2d6b] tracking-tight leading-tight">
              Your Cart
            </h1>
            <p className="text-sm text-slate-500 font-medium mt-1">
              Review inventory status and finalize your shipment request.
            </p>
          </div>

          {!isEmpty && (
            <div className="flex items-center gap-4 text-xs font-semibold">
              <Link 
                to="/" 
                className="inline-flex items-center gap-1.5 text-blue-700 hover:text-blue-800 transition-colors"
              >
                <ArrowLeft className="w-3.5 h-3.5" />
                <span>Continue Shopping</span>
              </Link>
              <button
                onClick={clearCart}
                className="text-slate-400 hover:text-rose-600 transition-colors cursor-pointer"
              >
                Clear Cart
              </button>
            </div>
          )}
        </div>

        {/* Validation Error Banner (Khi có sản phẩm hết hàng) */}
        {hasOutOfStock && (
          <div className="mb-6 rounded-2xl bg-[#fee2e2]/75 border border-[#fecaca] p-4.5 sm:p-5 flex items-start gap-3.5 shadow-xs transition-all animate-fade-in">
            <div className="w-5 h-5 rounded-full bg-[#991b1b] flex items-center justify-center text-white shrink-0 mt-0.5">
              <span className="text-xs font-black">!</span>
            </div>
            <div className="space-y-0.5">
              <h3 className="text-sm font-bold text-[#991b1b]">
                Validation Error
              </h3>
              <p className="text-xs sm:text-sm text-[#b91c1c] leading-relaxed">
                Some items in your cart are currently out of stock or exceed inventory limit. Please adjust quantities to proceed.
              </p>
            </div>
          </div>
        )}

        {/* Main Content: 2 Columns */}
        {isEmpty ? (
          /* Empty Cart State */
          <div className="bg-white rounded-3xl border border-slate-200/70 p-12 sm:p-16 text-center max-w-xl mx-auto shadow-xs my-8">
            <div className="w-20 h-20 mx-auto rounded-3xl bg-blue-50 border border-blue-100 flex items-center justify-center text-blue-600 mb-6 shadow-xs">
              <ShoppingBag className="w-10 h-10" />
            </div>
            <h2 className="text-xl sm:text-2xl font-black text-slate-900 tracking-tight">
              Your cart is empty
            </h2>
            <p className="text-sm text-slate-500 mt-2 mb-8 leading-relaxed">
              You haven't added any products from the catalog to your cart yet.
            </p>
            <Link
              to="/"
              className="inline-flex items-center justify-center gap-2 px-8 py-3.5 rounded-xl bg-[#0f3ea3] hover:bg-[#0c3384] active:scale-95 text-white font-bold text-sm shadow-md shadow-blue-600/20 transition-all"
            >
              <span>Explore Products</span>
              <ArrowRight className="w-4 h-4" />
            </Link>
          </div>
        ) : (
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
            
            {/* Left Column: Cart Items (7 cols) */}
            <div className="lg:col-span-7 space-y-4">
              {items.map((item) => {
                const stock = item.stockQuantity !== undefined ? item.stockQuantity : 999;
                const isOutOfStock = stock <= 0 || item.quantity > stock;
                const remainingStock = Math.max(0, stock - item.quantity);
                const isLimitedStock = !isOutOfStock && remainingStock <= 2;

                return (
                  <div
                    key={item.id}
                    className="flex items-center gap-2 sm:gap-3 group"
                  >
                    <button
                      onClick={() => handleRemoveItem(item.id)}
                      className="w-8 h-8 rounded-full flex items-center justify-center text-slate-400 hover:text-rose-600 hover:bg-rose-50 transition-all cursor-pointer shrink-0"
                      title="Hủy / Xóa sản phẩm"
                    >
                      <X className="w-5 h-5 stroke-[2.2]" />
                    </button>

                    <div
                      className={`flex-1 relative bg-white rounded-2xl p-5 shadow-xs transition-all flex flex-col sm:flex-row sm:items-center justify-between gap-5 ${
                        isOutOfStock
                          ? 'border border-dashed border-rose-300 bg-rose-50/15'
                          : 'border border-slate-200/70 hover:border-slate-300'
                      }`}
                    >
                      <div className="flex items-start sm:items-center gap-4">
                        <div className="w-18 h-18 sm:w-20 sm:h-20 rounded-xl overflow-hidden bg-slate-900 border border-slate-800 shrink-0 flex items-center justify-center relative shadow-xs">
                          {item.imageUrl ? (
                            <img
                              src={item.imageUrl}
                              alt={item.productName}
                              className="w-full h-full object-cover"
                            />
                          ) : (
                            <div className="w-full h-full bg-slate-800 flex items-center justify-center text-slate-500 text-xs font-bold">
                              NO IMAGE
                            </div>
                          )}
                        </div>

                        <div className="space-y-3">
                          <div>
                            <h2 className="text-base font-bold text-slate-900 leading-snug">
                              {item.productName}
                            </h2>
                            <p className="text-xs font-semibold text-slate-400 mt-0.5 tracking-wide">
                              SKU: {item.sku || `ITEM-${item.productVariantId?.substring(0, 8)?.toUpperCase() || 'DEF'}`}
                            </p>
                          </div>

                          <div className="inline-flex items-center bg-[#eef2f6] rounded-lg px-2.5 py-1 gap-3">
                            <button
                              onClick={() => handleUpdateQuantity(item, item.quantity - 1)}
                              disabled={loading}
                              className="text-slate-500 hover:text-slate-900 disabled:opacity-30 disabled:cursor-not-allowed transition-colors cursor-pointer p-0.5"
                              title="Decrease quantity"
                            >
                              <Minus className="w-3.5 h-3.5 stroke-[2.5]" />
                            </button>
                            <span className="text-sm font-bold text-slate-800 min-w-[20px] text-center select-none">
                              {item.quantity}
                            </span>
                            <button
                              onClick={() => handleUpdateQuantity(item, item.quantity + 1)}
                              disabled={loading || (stock !== undefined && item.quantity >= stock)}
                              className="text-slate-500 hover:text-slate-900 disabled:opacity-30 disabled:cursor-not-allowed transition-colors cursor-pointer p-0.5"
                              title={item.quantity >= stock ? `Reached max available stock (${stock})` : "Increase quantity"}
                            >
                              <Plus className="w-3.5 h-3.5 stroke-[2.5]" />
                            </button>
                          </div>
                        </div>
                      </div>

                      <div className="flex sm:flex-col items-center sm:items-end justify-between sm:justify-center border-t sm:border-t-0 pt-3 sm:pt-0 border-slate-100 gap-2.5">
                        <div className="text-left sm:text-right">
                          <div className="text-lg sm:text-xl font-bold text-[#0d2a75] tracking-tight">
                            {formatCurrency(item.price)}
                          </div>
                          <div className="text-[10px] font-extrabold text-slate-400 tracking-wider uppercase mt-0.5">
                            UNIT PRICE
                          </div>
                        </div>

                        <div className="mt-0 sm:mt-1">
                          {isOutOfStock ? (
                            <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold bg-[#fee2e2] text-[#991b1b] shadow-xs">
                              <span className="w-1.5 h-1.5 rounded-full bg-[#ef4444]" />
                              <span>OUT OF STOCK</span>
                            </span>
                          ) : isLimitedStock ? (
                            <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold bg-[#fef3c7] text-[#92400e] shadow-xs">
                              <span className="w-1.5 h-1.5 rounded-full bg-[#f59e0b]" />
                              <span>LIMITED STOCK ({remainingStock} LEFT)</span>
                            </span>
                          ) : (
                            <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold bg-[#dcfce7] text-[#15803d] shadow-xs">
                              <span className="w-1.5 h-1.5 rounded-full bg-[#22c55e]" />
                              <span>IN STOCK</span>
                            </span>
                          )}
                        </div>
                      </div>

                    </div>
                  </div>
                );
              })}
            </div>

            {/* Right Column: Order Summary (5 cols) */}
            <div className="lg:col-span-5 lg:sticky lg:top-8 space-y-4">
              
              {/* Card Summary */}
              <div className="bg-[#f8fafc] border border-slate-200/70 rounded-3xl p-6 sm:p-7 space-y-6 shadow-xs">
                <h2 className="text-xl font-bold text-[#0f2d6b] tracking-tight">
                  Order Summary
                </h2>

                {/* Voucher Section */}
                <div className="pt-2 pb-4 border-b border-slate-200/60 space-y-3">
                  <div className="flex items-center justify-between">
                    <span className="text-xs font-bold text-slate-600 flex items-center gap-1.5">
                      <Tag className="w-3.5 h-3.5 text-blue-600" />
                      <span>Promotion & Voucher</span>
                    </span>
                    <button
                      onClick={handleOpenVoucherModal}
                      className="text-xs font-semibold text-blue-600 hover:text-blue-800 transition-colors cursor-pointer"
                    >
                      View Available
                    </button>
                  </div>

                  {appliedDiscount ? (
                    <div className="flex items-center justify-between p-3 rounded-xl bg-emerald-50 border border-emerald-200">
                      <div className="flex items-center gap-2">
                        <Ticket className="w-4 h-4 text-emerald-600" />
                        <div>
                          <div className="text-xs font-bold text-emerald-800">
                            {appliedDiscount.code} - {appliedDiscount.name}
                          </div>
                          <div className="text-[11px] text-emerald-600">
                            Saved {formatCurrency(discountAmount)}
                          </div>
                        </div>
                      </div>
                      <button
                        onClick={removeDiscount}
                        disabled={loading}
                        className="text-slate-400 hover:text-rose-600 transition-colors p-1 cursor-pointer"
                        title="Remove voucher"
                      >
                        <X className="w-4 h-4" />
                      </button>
                    </div>
                  ) : (
                    <div className="flex items-center gap-2">
                      <input
                        type="text"
                        value={voucherCode}
                        onChange={(e) => setVoucherCode(e.target.value)}
                        placeholder="Enter coupon code"
                        className="flex-1 px-3.5 py-2 text-xs sm:text-sm bg-white border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 uppercase font-mono"
                      />
                      <button
                        onClick={() => handleApplyVoucher()}
                        disabled={isApplyingVoucher || !voucherCode.trim()}
                        className="px-4 py-2 text-xs font-bold bg-[#0f3ea3] hover:bg-[#0c3384] text-white rounded-xl transition-all disabled:opacity-40 disabled:cursor-not-allowed cursor-pointer shrink-0"
                      >
                        {isApplyingVoucher ? 'Applying...' : 'Apply'}
                      </button>
                    </div>
                  )}
                </div>

                {/* Cost Rows */}
                <div className="space-y-3.5 text-sm">
                  <div className="flex items-center justify-between text-slate-600">
                    <span className="font-medium">Subtotal</span>
                    <span className="font-bold text-slate-900">
                      {formatCurrency(rawSubtotal)}
                    </span>
                  </div>

                  {discountAmount > 0 && (
                    <div className="flex items-center justify-between text-slate-600">
                      <span className="font-medium">Voucher Discount</span>
                      <span className="font-bold text-[#10b981]">
                        -{formatCurrency(discountAmount)}
                      </span>
                    </div>
                  )}

                  <div className="flex items-center justify-between text-slate-600">
                    <span className="font-medium">Expedited Shipping</span>
                    <span className="font-bold text-slate-900">
                      {shippingFee > 0 ? formatCurrency(shippingFee) : 'FREE'}
                    </span>
                  </div>
                </div>

                {/* Total Amount */}
                <div className="pt-5 border-t border-slate-200/60">
                  <span className="text-[10px] font-extrabold text-slate-400 tracking-wider uppercase block mb-1">
                    TOTAL AMOUNT
                  </span>
                  <div className="flex items-baseline justify-between">
                    <span className="text-2xl sm:text-[32px] font-black text-[#0d2a75] tracking-tight">
                      {formatCurrency(totalAmount)}
                    </span>
                  </div>
                </div>

                {/* Actions */}
                <div className="space-y-3 pt-2">
                  <button
                    onClick={handleOpenCheckout}
                    className="w-full py-3.5 px-6 rounded-xl bg-[#0f3ea3] hover:bg-[#0c3384] active:scale-[0.99] text-white font-bold text-sm sm:text-base flex items-center justify-center gap-2 shadow-sm transition-all cursor-pointer"
                  >
                    <span>Place Order</span>
                    <ArrowRight className="w-4 h-4 stroke-[2.5]" />
                  </button>

                  <button
                    onClick={handleRevalidate}
                    disabled={isValidating}
                    className="w-full py-2.5 text-xs sm:text-sm font-semibold text-slate-600 hover:text-slate-900 flex items-center justify-center gap-2 transition-colors cursor-pointer disabled:opacity-50"
                  >
                    <RefreshCw className={`w-4 h-4 text-slate-500 ${isValidating ? 'animate-spin text-blue-600' : ''}`} />
                    <span>Re-validate Inventory</span>
                  </button>
                </div>

                {/* Logistics Guarantee Badge */}
                <div className="bg-[#dbeafe]/70 border border-blue-100 rounded-2xl p-4 flex items-start gap-3 mt-4">
                  <ShieldCheck className="w-5 h-5 text-[#1d4ed8] shrink-0 mt-0.5" />
                  <div className="space-y-0.5">
                    <h3 className="text-[11px] font-black text-[#1e40af] tracking-wider uppercase">
                      LOGISTICS GUARANTEE
                    </h3>
                    <p className="text-xs text-slate-600 leading-relaxed font-normal">
                      Real-time inventory lock and official warranty applied upon checkout.
                    </p>
                  </div>
                </div>

              </div>
            </div>

          </div>
        )}

      </div>

      {/* Available Vouchers Modal */}
      {isVoucherModalOpen && (
        <div className="fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl max-w-md w-full p-6 shadow-2xl space-y-5 animate-scale-up">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <div className="flex items-center gap-2">
                <Ticket className="w-5 h-5 text-blue-600" />
                <h3 className="text-lg font-bold text-[#0f2d6b]">Available Vouchers</h3>
              </div>
              <button 
                onClick={() => setIsVoucherModalOpen(false)}
                className="text-slate-400 hover:text-slate-600 p-1 cursor-pointer"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <div className="max-h-80 overflow-y-auto space-y-3 pr-1">
              {loadingVouchers ? (
                <div className="py-8 text-center text-sm text-slate-400">Loading vouchers...</div>
              ) : availableVouchers.length === 0 ? (
                <div className="py-8 text-center text-sm text-slate-400">No vouchers available for your account.</div>
              ) : (
                availableVouchers.map((v) => (
                  <div 
                    key={v.id} 
                    className="p-3.5 rounded-2xl border border-slate-200 hover:border-blue-300 hover:bg-blue-50/30 transition-all flex items-center justify-between gap-3"
                  >
                    <div className="space-y-1">
                      <div className="flex items-center gap-2">
                        <span className="font-mono text-xs font-extrabold px-2 py-0.5 rounded-md bg-blue-100 text-blue-800">
                          {v.code}
                        </span>
                        <span className="text-xs font-bold text-slate-800">{v.name}</span>
                      </div>
                      <p className="text-xs text-slate-500 leading-snug">{v.description || 'Applies to your order.'}</p>
                      {v.minOrderValue > 0 && (
                        <p className="text-[11px] text-slate-400">Min order: {formatCurrency(v.minOrderValue)}</p>
                      )}
                    </div>
                    <button
                      onClick={() => handleApplyVoucher(v.code)}
                      className="px-3.5 py-1.5 text-xs font-bold rounded-lg bg-blue-600 hover:bg-blue-700 text-white transition-all cursor-pointer shrink-0"
                    >
                      Use
                    </button>
                  </div>
                ))
              )}
            </div>
          </div>
        </div>
      )}

      {/* Checkout Form Modal */}
      {isCheckoutModalOpen && (
        <div className="fixed inset-0 z-50 bg-slate-900/50 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl max-w-lg w-full p-6 sm:p-8 shadow-2xl space-y-5 animate-scale-up max-h-[90vh] overflow-y-auto">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <div className="flex items-center gap-2">
                <Truck className="w-5 h-5 text-blue-600" />
                <h3 className="text-lg font-bold text-[#0f2d6b]">Shipping & Checkout</h3>
              </div>
              <button 
                onClick={() => setIsCheckoutModalOpen(false)}
                className="text-slate-400 hover:text-slate-600 p-1 cursor-pointer"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSubmitOrder} className="space-y-4">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <div>
                  <label className="text-xs font-bold text-slate-600 block mb-1">Receiver Name *</label>
                  <input
                    type="text"
                    required
                    value={shippingForm.shippingName}
                    onChange={(e) => setShippingForm({ ...shippingForm, shippingName: e.target.value })}
                    placeholder="Nguyen Van A"
                    className="w-full px-3 py-2 text-xs sm:text-sm bg-slate-50 border border-slate-200 rounded-xl focus:bg-white focus:outline-none focus:ring-2 focus:ring-blue-500/20"
                  />
                </div>
                <div>
                  <label className="text-xs font-bold text-slate-600 block mb-1">Phone Number *</label>
                  <input
                    type="tel"
                    required
                    value={shippingForm.shippingPhone}
                    onChange={(e) => setShippingForm({ ...shippingForm, shippingPhone: e.target.value })}
                    placeholder="0901234567"
                    className="w-full px-3 py-2 text-xs sm:text-sm bg-slate-50 border border-slate-200 rounded-xl focus:bg-white focus:outline-none focus:ring-2 focus:ring-blue-500/20"
                  />
                </div>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                <div>
                  <label className="text-xs font-bold text-slate-600 block mb-1">Province *</label>
                  <input
                    type="text"
                    required
                    value={shippingForm.shippingProvince}
                    onChange={(e) => setShippingForm({ ...shippingForm, shippingProvince: e.target.value })}
                    className="w-full px-3 py-2 text-xs sm:text-sm bg-slate-50 border border-slate-200 rounded-xl focus:bg-white focus:outline-none focus:ring-2 focus:ring-blue-500/20"
                  />
                </div>
                <div>
                  <label className="text-xs font-bold text-slate-600 block mb-1">District *</label>
                  <input
                    type="text"
                    required
                    value={shippingForm.shippingDistrict}
                    onChange={(e) => setShippingForm({ ...shippingForm, shippingDistrict: e.target.value })}
                    className="w-full px-3 py-2 text-xs sm:text-sm bg-slate-50 border border-slate-200 rounded-xl focus:bg-white focus:outline-none focus:ring-2 focus:ring-blue-500/20"
                  />
                </div>
                <div>
                  <label className="text-xs font-bold text-slate-600 block mb-1">Ward *</label>
                  <input
                    type="text"
                    required
                    value={shippingForm.shippingWard}
                    onChange={(e) => setShippingForm({ ...shippingForm, shippingWard: e.target.value })}
                    className="w-full px-3 py-2 text-xs sm:text-sm bg-slate-50 border border-slate-200 rounded-xl focus:bg-white focus:outline-none focus:ring-2 focus:ring-blue-500/20"
                  />
                </div>
              </div>

              <div>
                <label className="text-xs font-bold text-slate-600 block mb-1">Street Address *</label>
                <input
                  type="text"
                  required
                  value={shippingForm.shippingStreet}
                  onChange={(e) => setShippingForm({ ...shippingForm, shippingStreet: e.target.value })}
                  placeholder="123 Le Loi, Suite 402"
                  className="w-full px-3 py-2 text-xs sm:text-sm bg-slate-50 border border-slate-200 rounded-xl focus:bg-white focus:outline-none focus:ring-2 focus:ring-blue-500/20"
                />
              </div>

              <div>
                <label className="text-xs font-bold text-slate-600 block mb-1">Order Note (Optional)</label>
                <textarea
                  rows={2}
                  value={shippingForm.note}
                  onChange={(e) => setShippingForm({ ...shippingForm, note: e.target.value })}
                  placeholder="Delivery instructions (e.g. call before arriving)"
                  className="w-full px-3 py-2 text-xs sm:text-sm bg-slate-50 border border-slate-200 rounded-xl focus:bg-white focus:outline-none focus:ring-2 focus:ring-blue-500/20"
                />
              </div>

              <div>
                <label className="text-xs font-bold text-slate-600 block mb-1">Payment Method</label>
                <div className="grid grid-cols-2 gap-3">
                  <label className={`flex items-center gap-2 p-3 rounded-xl border cursor-pointer transition-all ${shippingForm.paymentMethod === 'COD' ? 'border-blue-600 bg-blue-50/40 text-blue-900 font-bold' : 'border-slate-200 text-slate-600'}`}>
                    <input
                      type="radio"
                      name="paymentMethod"
                      value="COD"
                      checked={shippingForm.paymentMethod === 'COD'}
                      onChange={(e) => setShippingForm({ ...shippingForm, paymentMethod: e.target.value })}
                      className="accent-blue-600"
                    />
                    <span className="text-xs">COD (Cash on Delivery)</span>
                  </label>

                  <label className={`flex items-center gap-2 p-3 rounded-xl border cursor-pointer transition-all ${shippingForm.paymentMethod === 'VNPAY' ? 'border-blue-600 bg-blue-50/40 text-blue-900 font-bold' : 'border-slate-200 text-slate-600'}`}>
                    <input
                      type="radio"
                      name="paymentMethod"
                      value="VNPAY"
                      checked={shippingForm.paymentMethod === 'VNPAY'}
                      onChange={(e) => setShippingForm({ ...shippingForm, paymentMethod: e.target.value })}
                      className="accent-blue-600"
                    />
                    <span className="text-xs">Online Banking / VNPay</span>
                  </label>
                </div>
              </div>

              {/* Order total preview */}
              <div className="p-3.5 rounded-xl bg-slate-50 border border-slate-200 flex items-center justify-between text-sm">
                <span className="font-medium text-slate-600">Grand Total:</span>
                <span className="text-lg font-black text-[#0d2a75]">{formatCurrency(totalAmount)}</span>
              </div>

              <button
                type="submit"
                disabled={isSubmittingOrder}
                className="w-full py-3.5 rounded-xl bg-[#0f3ea3] hover:bg-[#0c3384] text-white font-bold text-sm shadow-md transition-all cursor-pointer disabled:opacity-50"
              >
                {isSubmittingOrder ? 'Processing Order...' : 'Confirm & Place Order'}
              </button>
            </form>
          </div>
        </div>
      )}

      {/* Order Success Modal */}
      {orderSuccess && (
        <div className="fixed inset-0 z-50 bg-slate-900/50 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl max-w-md w-full p-8 text-center space-y-5 shadow-2xl animate-scale-up">
            <div className="w-16 h-16 mx-auto rounded-full bg-emerald-100 flex items-center justify-center text-emerald-600">
              <CheckCircle2 className="w-10 h-10" />
            </div>
            <div>
              <h2 className="text-2xl font-black text-[#0f2d6b]">Order Placed!</h2>
              <p className="text-xs sm:text-sm text-slate-500 mt-1">
                Your order has been recorded successfully.
              </p>
            </div>

            <div className="p-4 rounded-2xl bg-slate-50 border border-slate-200 text-left space-y-2 text-xs">
              <div className="flex justify-between">
                <span className="text-slate-500">Order ID:</span>
                <span className="font-mono font-bold text-slate-800">{orderSuccess.id}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-500">Recipient:</span>
                <span className="font-bold text-slate-800">{orderSuccess.shippingName}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-500">Total Paid:</span>
                <span className="font-bold text-[#0d2a75]">{formatCurrency(orderSuccess.grandTotal)}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-500">Payment:</span>
                <span className="font-bold text-slate-800">{orderSuccess.paymentMethod || 'COD'}</span>
              </div>
            </div>

            <button
              onClick={() => setOrderSuccess(null)}
              className="w-full py-3 rounded-xl bg-[#0f3ea3] hover:bg-[#0c3384] text-white font-bold text-sm transition-all cursor-pointer"
            >
              Continue Shopping
            </button>
          </div>
        </div>
      )}

      {/* Bottom Bar: Logistics Precision */}
      <footer className="mt-16 bg-white border-t border-slate-200/80 shadow-xs">
        <div className="max-w-[1240px] mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between gap-6">
          <div className="flex items-center gap-2">
            <span className="text-base sm:text-lg font-black tracking-tight text-[#0f2d6b]">
              Logistics Precision
            </span>
          </div>

          <nav className="hidden md:flex items-center gap-8 text-sm font-semibold text-slate-500">
            <a href="#orders" className="hover:text-slate-900 transition-colors">Orders</a>
            <a href="#shipments" className="hover:text-slate-900 transition-colors">Shipments</a>
            <a href="#analytics" className="hover:text-slate-900 transition-colors">Analytics</a>
            <a href="#customers" className="hover:text-slate-900 transition-colors">Customers</a>
          </nav>

          <div className="flex items-center gap-4">
            <button className="text-slate-400 hover:text-slate-700 transition-colors p-1.5 cursor-pointer">
              <Bell className="w-4 h-4" />
            </button>
            <button className="text-slate-400 hover:text-slate-700 transition-colors p-1.5 cursor-pointer">
              <Settings className="w-4 h-4" />
            </button>
            <div className="w-8 h-8 rounded-full overflow-hidden border border-slate-200 bg-amber-100 flex items-center justify-center text-xs font-bold text-amber-800">
              <img
                src="https://images.unsplash.com/photo-1494790108377-be9c29b29330?q=80&w=120&auto=format&fit=crop"
                alt="User profile"
                className="w-full h-full object-cover"
              />
            </div>
          </div>
        </div>
      </footer>

    </div>
  );
};
