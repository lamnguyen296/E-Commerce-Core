import React from 'react';
import { useLocation } from 'react-router-dom';
import { Navbar } from '../components/Navbar';
import { Footer } from '../components/Footer';

export const MainLayout = ({ children }) => {
  const location = useLocation();
  const isCart = location.pathname === '/cart';
  const isReturns = location.pathname.startsWith('/returns') || location.pathname.startsWith('/admin');

  if (isCart || isReturns) {
    return <main className="min-h-screen bg-[#f4f6fb]">{children}</main>;
  }

  return (
    <div className="min-h-screen flex flex-col bg-slate-50 selection:bg-blue-100 selection:text-blue-900">
      <Navbar />
      <main className="flex-1">{children}</main>
      <Footer />
    </div>
  );
};

