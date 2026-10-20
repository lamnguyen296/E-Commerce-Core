import React, { useState, useEffect } from 'react';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import { ProductCard } from './ProductCard';
import { productService } from '../services/productService';

const FALLBACK_FEATURED_PRODUCTS = [
  {
    id: 'prod-air-max-270',
    name: 'Air Max 270 React Blue',
    brand: 'NIKE',
    price: 2450000,
    originalPrice: 3100000,
    imageUrl: 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?q=80&w=800&auto=format&fit=crop',
    inStock: true,
    stockQuantity: 35,
    defaultVariantId: 'var-air-max-270-default',
  },
  {
    id: 'prod-sony-wh1000xm4',
    name: 'WH-1000XM4 Noise Canceling',
    brand: 'SONY',
    price: 8990000,
    originalPrice: null,
    imageUrl: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?q=80&w=800&auto=format&fit=crop',
    inStock: false,
    stockQuantity: 0,
    defaultVariantId: 'var-sony-wh1000xm4-default',
  },
  {
    id: 'prod-seiko-presage',
    name: 'Presage Cocktail Time',
    brand: 'SEIKO',
    price: 12500000,
    originalPrice: null,
    imageUrl: 'https://images.unsplash.com/photo-1524805444758-089113d48a6d?q=80&w=800&auto=format&fit=crop',
    inStock: true,
    stockQuantity: 10,
    defaultVariantId: 'var-seiko-presage-default',
  },
  {
    id: 'prod-rayban-aviator',
    name: 'Classic Aviator Gold',
    brand: 'RAY-BAN',
    price: 4200000,
    originalPrice: 4900000,
    imageUrl: 'https://images.unsplash.com/photo-1511499767150-a48a237f0083?q=80&w=800&auto=format&fit=crop',
    inStock: true,
    stockQuantity: 20,
    defaultVariantId: 'var-rayban-aviator-default',
  },
];

export const ProductList = () => {
  const [products, setProducts] = useState(FALLBACK_FEATURED_PRODUCTS);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setLoading(true);
    productService
      .getFeaturedProducts()
      .then((data) => {
        if (data && data.length > 0) {
          setProducts(data);
        }
      })
      .catch((err) => {
        console.warn('Backend products not reachable, using fallback catalog:', err);
      })
      .finally(() => {
        setLoading(false);
      });
  }, []);

  return (
    <section id="products" className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      {/* Header */}
      <div className="flex items-center justify-between mb-8">
        <h2 className="text-2xl sm:text-3xl font-extrabold text-slate-900">
          Featured Products
        </h2>

        {/* Carousel Controls */}
        <div className="flex items-center gap-2">
          <button
            className="w-9 h-9 rounded-full border border-slate-200 flex items-center justify-center text-slate-600 hover:bg-slate-100 hover:text-slate-900 transition-colors"
            title="Previous product"
          >
            <ChevronLeft className="w-5 h-5" />
          </button>
          <button
            className="w-9 h-9 rounded-full border border-slate-200 flex items-center justify-center text-slate-600 hover:bg-slate-100 hover:text-slate-900 transition-colors"
            title="Next product"
          >
            <ChevronRight className="w-5 h-5" />
          </button>
        </div>
      </div>

      {/* Product Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
        {products.map((product) => (
          <ProductCard key={product.id} product={product} />
        ))}
      </div>
    </section>
  );
};
