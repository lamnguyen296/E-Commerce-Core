import React from 'react';
import { CheckCircle2, AlertCircle, X } from 'lucide-react';

export const Toast = ({ toast, onClose }) => {
  if (!toast || !toast.show) return null;

  const isSuccess = toast.type === 'success';

  return (
    <div className="fixed bottom-6 right-6 z-50 flex items-center gap-3 px-5 py-3.5 rounded-xl shadow-2xl bg-white border border-slate-200 transition-all transform animate-slide-up">
      {isSuccess ? (
        <CheckCircle2 className="w-5 h-5 text-emerald-600 shrink-0" />
      ) : (
        <AlertCircle className="w-5 h-5 text-rose-600 shrink-0" />
      )}
      <div className="text-sm font-medium text-slate-800">
        {toast.message}
      </div>
      <button
        onClick={onClose}
        className="ml-2 text-slate-400 hover:text-slate-600 p-1 transition-colors"
      >
        <X className="w-4 h-4" />
      </button>
    </div>
  );
};
