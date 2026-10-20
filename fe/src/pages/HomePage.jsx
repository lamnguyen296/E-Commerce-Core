import React from 'react';
import { HeroBanner } from '../components/HeroBanner';
import { CategorySection } from '../components/CategorySection';
import { ProductList } from '../components/ProductList';
import { Newsletter } from '../components/Newsletter';

export const HomePage = () => {
  return (
    <div className="space-y-8 pb-12">
      <HeroBanner />
      <CategorySection />
      <ProductList />
      <Newsletter />
    </div>
  );
};
