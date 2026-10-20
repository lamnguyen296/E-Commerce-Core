import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { MainLayout } from '../layouts/MainLayout';
import { HomePage } from '../pages/HomePage';
import { CartPage } from '../pages/CartPage';
import { ReturnsManagementPage } from '../pages/ReturnsManagementPage';

export const AppRoutes = () => {
  return (
    <MainLayout>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/cart" element={<CartPage />} />
        <Route path="/returns" element={<ReturnsManagementPage />} />
        <Route path="/admin/returns" element={<ReturnsManagementPage />} />
      </Routes>
    </MainLayout>
  );
};

