import React from 'react';
import { Share2, Globe } from 'lucide-react';

export const Footer = () => {
  return (
    <footer className="bg-[#eef2ff] border-t border-blue-100/60 pt-16 pb-8 text-slate-600">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        {/* Main Columns */}
        <div className="grid grid-cols-1 md:grid-cols-12 gap-10 pb-12">
          
          {/* Brand Info */}
          <div className="md:col-span-4 space-y-4">
            <h3 className="text-xl font-extrabold text-slate-900 tracking-tight">
              Storefront
            </h3>
            <p className="text-sm text-slate-600 leading-relaxed max-w-sm">
              Leading online shopping destination delivering the finest technology and lifestyle goods worldwide.
            </p>
          </div>

          {/* Column: About Us */}
          <div className="md:col-span-2 space-y-3">
            <h4 className="text-sm font-bold text-slate-900 tracking-wide">
              About Us
            </h4>
            <ul className="space-y-2 text-sm">
              <li><a href="#about" className="hover:text-blue-600 transition-colors">Brand Story</a></li>
              <li><a href="#careers" className="hover:text-blue-600 transition-colors">Careers</a></li>
              <li><a href="#contact" className="hover:text-blue-600 transition-colors">Contact</a></li>
            </ul>
          </div>

          {/* Column: Customer Support */}
          <div className="md:col-span-3 space-y-3">
            <h4 className="text-sm font-bold text-slate-900 tracking-wide">
              Customer Support
            </h4>
            <ul className="space-y-2 text-sm">
              <li><a href="#shipping" className="hover:text-blue-600 transition-colors">Shipping Policy</a></li>
              <li><a href="#returns" className="hover:text-blue-600 transition-colors">Returns & Refunds</a></li>
              <li><a href="#faq" className="hover:text-blue-600 transition-colors">FAQ</a></li>
            </ul>
          </div>

          {/* Column: Connect */}
          <div className="md:col-span-3 space-y-3">
            <h4 className="text-sm font-bold text-slate-900 tracking-wide">
              Connect
            </h4>
            <div className="flex items-center gap-3 pt-1">
              <button className="w-10 h-10 rounded-xl bg-white shadow-xs border border-blue-100 flex items-center justify-center text-blue-600 hover:bg-blue-50 transition-colors cursor-pointer">
                <Globe className="w-4 h-4" />
              </button>
              <button className="w-10 h-10 rounded-xl bg-white shadow-xs border border-blue-100 flex items-center justify-center text-blue-600 hover:bg-blue-50 transition-colors cursor-pointer">
                <Share2 className="w-4 h-4" />
              </button>
            </div>
          </div>

        </div>

        {/* Bottom Bar */}
        <div className="pt-8 border-t border-blue-200/50 flex flex-col sm:flex-row items-center justify-between gap-4 text-xs font-medium text-slate-500">
          <p>© 2024 STOREFRONT. ALL RIGHTS RESERVED.</p>
          <div className="flex items-center gap-6">
            <a href="#terms" className="hover:text-slate-800 transition-colors">TERMS OF SERVICE</a>
            <a href="#privacy" className="hover:text-slate-800 transition-colors">PRIVACY POLICY</a>
          </div>
        </div>

      </div>
    </footer>
  );
};
