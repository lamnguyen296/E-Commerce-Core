import React, { useState, useEffect } from 'react';
import { ArrowUpRight } from 'lucide-react';
import { categoryService } from '../services/categoryService';

// Default categories fallback in English
const FALLBACK_CATEGORIES = [
  {
    id: 'cat-shoes',
    name: 'Fashion Footwear',
    itemCountLabel: 'Over 500+ latest models',
    imageUrl: 'https://images.unsplash.com/photo-1549298916-b41d501d3772?q=80&w=800&auto=format&fit=crop',
  },
  {
    id: 'cat-watches',
    name: 'Luxury Watches',
    itemCountLabel: 'Authentic Swiss & Japanese timepieces',
    imageUrl: 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?q=80&w=800&auto=format&fit=crop',
  },
  {
    id: 'cat-electronics',
    name: 'Electronics',
    itemCountLabel: '12-24 months warranty',
    imageUrl: 'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?q=80&w=800&auto=format&fit=crop',
  },
  {
    id: 'cat-accessories',
    name: 'Accessories',
    itemCountLabel: '2024 Fashion Trend',
    imageUrl: 'https://images.unsplash.com/photo-1572635196237-14b3f281503f?q=80&w=800&auto=format&fit=crop',
  },
];

export const CategorySection = () => {
  const [categories, setCategories] = useState(FALLBACK_CATEGORIES);

  useEffect(() => {
    categoryService
      .getFeaturedCategories()
      .then((data) => {
        if (data && data.length > 0) {
          setCategories(data);
        }
      })
      .catch((err) => {
        console.warn('Could not fetch categories from backend, using default fallback:', err);
      });
  }, []);

  const shoes = categories.find((c) => c.id === 'cat-shoes' || c.name.toLowerCase().includes('shoe') || c.name.toLowerCase().includes('footwear')) || categories[0];
  const watches = categories.find((c) => c.id === 'cat-watches' || c.name.toLowerCase().includes('watch')) || categories[1];
  const electronics = categories.find((c) => c.id === 'cat-electronics' || c.name.toLowerCase().includes('electronic')) || categories[2];
  const accessories = categories.find((c) => c.id === 'cat-accessories' || c.name.toLowerCase().includes('accessor')) || categories[3];

  return (
    <section id="categories" className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      {/* Header */}
      <div className="flex items-end justify-between mb-8">
        <div>
          <span className="text-xs font-bold text-blue-600 tracking-wider uppercase">
            DISCOVER
          </span>
          <h2 className="text-2xl sm:text-3xl font-extrabold text-slate-900 mt-1">
            Featured Categories
          </h2>
        </div>
        <a
          href="#all-categories"
          className="inline-flex items-center gap-1 text-sm font-semibold text-blue-600 hover:text-blue-700 transition-colors group"
        >
          <span>All Categories</span>
          <ArrowUpRight className="w-4 h-4 group-hover:translate-x-0.5 group-hover:-translate-y-0.5 transition-transform" />
        </a>
      </div>

      {/* Grid matching layout */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        
        {/* Left Column: Big Shoe Card */}
        <div className="lg:col-span-6 min-h-[380px] lg:min-h-[480px] relative rounded-2xl overflow-hidden group cursor-pointer shadow-md bg-slate-900">
          <img
            src={shoes?.imageUrl}
            alt={shoes?.name}
            className="w-full h-full object-cover object-center group-hover:scale-105 transition-transform duration-500"
          />
          <div className="absolute inset-0 bg-gradient-to-t from-black/85 via-black/20 to-transparent" />
          <div className="absolute bottom-6 left-6 right-6 text-white">
            <h3 className="text-2xl font-bold tracking-tight">{shoes?.name}</h3>
            <p className="text-sm text-slate-300 mt-1 font-medium">{shoes?.itemCountLabel}</p>
          </div>
        </div>

        {/* Right Column: 1 Top card + 2 Bottom cards */}
        <div className="lg:col-span-6 flex flex-col gap-6">
          
          {/* Top Card: Watches */}
          <div className="h-56 lg:h-[228px] relative rounded-2xl overflow-hidden group cursor-pointer shadow-md bg-slate-900">
            <img
              src={watches?.imageUrl}
              alt={watches?.name}
              className="w-full h-full object-cover object-center group-hover:scale-105 transition-transform duration-500"
            />
            <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-black/20 to-transparent" />
            <div className="absolute bottom-6 left-6 text-white">
              <h3 className="text-xl font-bold tracking-tight">{watches?.name}</h3>
              {watches?.itemCountLabel && (
                <p className="text-xs text-slate-300 mt-0.5 font-medium">{watches.itemCountLabel}</p>
              )}
            </div>
          </div>

          {/* Bottom Row: 2 Split Cards */}
          <div className="grid grid-cols-2 gap-6 h-52 lg:h-[228px]">
            {/* Electronics */}
            <div className="relative rounded-2xl overflow-hidden group cursor-pointer shadow-md bg-slate-900">
              <img
                src={electronics?.imageUrl}
                alt={electronics?.name}
                className="w-full h-full object-cover object-center group-hover:scale-105 transition-transform duration-500"
              />
              <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-black/20 to-transparent" />
              <div className="absolute bottom-4 left-4 text-white">
                <h3 className="text-lg font-bold tracking-tight">{electronics?.name}</h3>
              </div>
            </div>

            {/* Accessories */}
            <div className="relative rounded-2xl overflow-hidden group cursor-pointer shadow-md bg-slate-900">
              <img
                src={accessories?.imageUrl}
                alt={accessories?.name}
                className="w-full h-full object-cover object-center group-hover:scale-105 transition-transform duration-500"
              />
              <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-black/20 to-transparent" />
              <div className="absolute bottom-4 left-4 text-white">
                <h3 className="text-lg font-bold tracking-tight">{accessories?.name}</h3>
              </div>
            </div>
          </div>

        </div>

      </div>
    </section>
  );
};
