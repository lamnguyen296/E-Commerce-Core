import React, { useState } from 'react';
import { ShoppingCart, Check } from 'lucide-react';
import { formatCurrency } from '../utils/formatters';
import { useCart } from '../context/CartContext';

export const ProductCard = ({ product }) => {
  const { addToCart } = useCart();
  const [adding, setAdding] = useState(false);

  const inStock = product.inStock !== false && product.stockQuantity !== 0;
  const variantId = product.defaultVariantId || product.variants?.[0]?.id || `var-${product.id}`;

  const handleAddToCart = async (e) => {
    e.stopPropagation();
    if (!inStock || adding) return;

    setAdding(true);
    await addToCart(variantId, 1, product);
    setTimeout(() => {
      setAdding(false);
    }, 600);
  };

  return (
    <div className="group bg-white rounded-2xl border border-slate-100 p-3.5 hover:shadow-xl hover:border-slate-200 transition-all duration-300 flex flex-col justify-between">
      <div>
        {/* Image Container with Badge */}
        <div className="relative w-full aspect-square rounded-xl overflow-hidden bg-slate-900 mb-4 flex items-center justify-center">
          <img
            src={product.imageUrl}
            alt={product.name}
            className={`w-full h-full object-cover transition-transform duration-500 ${
              inStock ? 'group-hover:scale-105' : 'opacity-75 grayscale-20'
            }`}
          />

          {/* Stock Badge */}
          <div className="absolute top-3 left-3">
            {inStock ? (
              <span className="px-2.5 py-1 text-[11px] font-bold tracking-wider uppercase rounded-full bg-emerald-700/90 text-white backdrop-blur-xs shadow-xs">
                IN STOCK
              </span>
            ) : (
              <span className="px-2.5 py-1 text-[11px] font-bold tracking-wider uppercase rounded-full bg-rose-500/90 text-white backdrop-blur-xs shadow-xs">
                OUT OF STOCK
              </span>
            )}
          </div>
        </div>

        {/* Brand */}
        <div className="text-[11px] font-bold tracking-wider text-slate-400 uppercase mb-1">
          {product.brand || product.category?.name || 'STOREFRONT'}
        </div>

        {/* Title */}
        <h3 className="text-sm font-bold text-slate-800 line-clamp-1 group-hover:text-blue-600 transition-colors">
          {product.name}
        </h3>
      </div>

      {/* Price and Add to Cart Section */}
      <div className="mt-4 pt-3 border-t border-slate-100 flex items-center justify-between">
        <div className="flex flex-col">
          <div className="flex items-center gap-2">
            <span className="text-base font-extrabold text-blue-700">
              {formatCurrency(product.price)}
            </span>
            {product.originalPrice && (
              <span className="text-xs text-slate-400 line-through font-medium">
                {formatCurrency(product.originalPrice)}
              </span>
            )}
          </div>
        </div>

        {/* Add To Cart Button */}
        <button
          onClick={handleAddToCart}
          disabled={!inStock || adding}
          className={`p-2.5 rounded-xl transition-all duration-200 flex items-center justify-center ${
            !inStock
              ? 'bg-slate-100 text-slate-300 cursor-not-allowed'
              : adding
              ? 'bg-emerald-600 text-white shadow-md'
              : 'bg-blue-600 hover:bg-blue-700 active:scale-95 text-white shadow-md shadow-blue-500/20'
          }`}
          title={inStock ? 'Add to Cart' : 'Out of stock'}
        >
          {adding ? (
            <Check className="w-4 h-4 animate-scale" />
          ) : (
            <ShoppingCart className="w-4 h-4" />
          )}
        </button>
      </div>
    </div>
  );
};
