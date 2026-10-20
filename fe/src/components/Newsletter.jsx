import React, { useState } from 'react';
import { Mail } from 'lucide-react';
import { useCart } from '../context/CartContext';

export const Newsletter = () => {
  const [email, setEmail] = useState('');
  const { showToast } = useCart();

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!email) return;
    showToast('Thank you for subscribing! A 10% discount code has been sent to your email.', 'success');
    setEmail('');
  };

  return (
    <section className="max-w-5xl mx-auto px-4 sm:px-6 my-16">
      <div className="bg-[#f0f4ff] rounded-3xl p-8 sm:p-12 text-center flex flex-col items-center border border-blue-100 shadow-xs">
        
        {/* Mail Icon */}
        <div className="w-12 h-12 rounded-full bg-blue-100 flex items-center justify-center text-blue-600 mb-4 shadow-xs">
          <Mail className="w-6 h-6" />
        </div>

        {/* Heading */}
        <h2 className="text-2xl sm:text-3xl font-extrabold text-slate-900 tracking-tight">
          Exclusive Offers
        </h2>

        {/* Description */}
        <p className="text-slate-600 text-sm sm:text-base max-w-lg mt-2 mb-8 font-medium leading-relaxed">
          Subscribe to our newsletter for early access to new collections and a 10% discount on your first order.
        </p>

        {/* Form */}
        <form onSubmit={handleSubmit} className="w-full max-w-md flex flex-col sm:flex-row gap-3">
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="Enter your email address"
            required
            className="flex-1 px-5 py-3 rounded-xl bg-white border border-slate-200 text-sm text-slate-800 placeholder-slate-400 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 shadow-xs"
          />
          <button
            type="submit"
            className="px-6 py-3 rounded-xl bg-[#0f2b82] hover:bg-[#0b2062] text-white font-semibold text-sm transition-colors shadow-md shadow-blue-900/10 cursor-pointer"
          >
            Subscribe Now
          </button>
        </form>

      </div>
    </section>
  );
};
