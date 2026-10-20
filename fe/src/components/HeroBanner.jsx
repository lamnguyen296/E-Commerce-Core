import React from 'react';
import { ArrowRight } from 'lucide-react';

export const HeroBanner = () => {
  return (
    <section className="relative overflow-hidden rounded-2xl bg-gradient-to-r from-slate-900 via-indigo-950 to-blue-900 text-white my-6 mx-4 sm:mx-6 lg:mx-8 shadow-xl">
      {/* Decorative background glow */}
      <div className="absolute -top-24 -left-24 w-96 h-96 bg-blue-600/30 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute -bottom-24 -right-24 w-96 h-96 bg-indigo-500/20 rounded-full blur-3xl pointer-events-none" />

      <div className="max-w-7xl mx-auto px-6 sm:px-12 py-16 lg:py-20 grid grid-cols-1 lg:grid-cols-12 gap-8 items-center relative z-10">
        
        {/* Text Content */}
        <div className="lg:col-span-7 space-y-6">
          {/* Badge */}
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-emerald-950/80 border border-emerald-500/40 text-emerald-400 text-xs font-semibold tracking-wider uppercase">
            <span className="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-pulse" />
            COLLECTION 2024
          </div>

          {/* Heading */}
          <h1 className="text-4xl sm:text-5xl lg:text-6xl font-black tracking-tight leading-[1.15]">
            Defining <br />
            <span className="text-transparent bg-clip-text bg-gradient-to-r from-blue-200 via-white to-slate-200">
              Precision.
            </span>
          </h1>

          {/* Subtitle */}
          <p className="text-slate-300 text-base sm:text-lg max-w-xl font-normal leading-relaxed">
            Discover premier lifestyle, cutting-edge technology, and curated footwear designed for modern everyday living.
          </p>

          {/* CTA Buttons */}
          <div className="flex flex-wrap items-center gap-4 pt-2">
            <a
              href="#products"
              className="inline-flex items-center gap-2 px-6 py-3 rounded-xl bg-white text-slate-900 font-semibold text-sm hover:bg-slate-100 hover:shadow-lg hover:shadow-white/10 transition-all duration-200 group"
            >
              <span>Shop Now</span>
              <ArrowRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
            </a>

            <a
              href="#categories"
              className="inline-flex items-center gap-2 px-6 py-3 rounded-xl bg-slate-800/80 hover:bg-slate-700/80 text-white font-medium text-sm border border-slate-700/50 backdrop-blur-xs transition-all duration-200"
            >
              Browse Categories
            </a>
          </div>
        </div>

        {/* Hero Visual */}
        <div className="lg:col-span-5 relative flex justify-center lg:justify-end">
          <div className="relative w-72 h-80 sm:w-80 sm:h-96 rounded-2xl overflow-hidden shadow-2xl border border-white/10">
            <img
              src="https://images.unsplash.com/photo-1617137984095-74e4e5e3613f?q=80&w=800&auto=format&fit=crop"
              alt="Fashion Collection 2024"
              className="w-full h-full object-cover object-top hover:scale-105 transition-transform duration-700"
            />
            {/* Overlay Gradient */}
            <div className="absolute inset-0 bg-gradient-to-t from-slate-950/80 via-transparent to-transparent" />
            
            {/* Floating Logo Badge */}
            <div className="absolute top-4 right-4 w-12 h-12 rounded-full bg-blue-600/90 backdrop-blur-md border border-white/30 flex items-center justify-center shadow-lg">
              <span className="text-white font-black text-xl tracking-wider">B</span>
            </div>
          </div>
        </div>

      </div>
    </section>
  );
};
