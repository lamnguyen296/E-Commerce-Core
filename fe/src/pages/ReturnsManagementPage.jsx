import React, { useState, useMemo, useRef } from 'react';
import {
  LayoutGrid,
  RotateCcw,
  Truck,
  Warehouse,
  BarChart3,
  HelpCircle,
  LogOut,
  Search,
  Bell,
  Settings,
  Download,
  Upload,
  FileSpreadsheet,
  CornerUpLeft,
  ClipboardCheck,
  Banknote,
  TrendingUp,
  Zap,
  User,
  ShieldAlert,
  Sparkles,
  AlertTriangle,
  ChevronLeft,
  ChevronRight,
  Plus,
  X,
  CheckCircle2,
  Package,
  Calendar,
  Clock,
  ArrowRight,
} from 'lucide-react';
import { returnService } from '../services/returnService';

const INITIAL_RETURNS = [
  {
    id: '#RET-8842',
    rawId: 'RET-8842',
    initiated: '2m ago',
    date: '2026-09-21 18:39',
    customer: 'Eleanor Shellstrop',
    email: 'eleanor@shellstrop.org',
    orderNumber: 'Order #ORD-9921-X',
    reason: 'Damaged on arrival',
    originType: 'CUSTOMER REQUESTED',
    status: 'IN-TRANSIT',
    itemCount: 2,
    refundAmount: '$189.50',
    product: 'Ergonomic Executive Chair (Midnight Navy)',
    carrier: 'FedEx Priority',
    trackingNumber: 'TRK-98234-US'
  },
  {
    id: '#RET-8841',
    rawId: 'RET-8841',
    initiated: '45m ago',
    date: '2026-09-21 17:56',
    customer: 'Chidi Anagonye',
    email: 'chidi.ethics@univ.edu',
    orderNumber: 'Order #ORD-4420-Y',
    reason: 'Failed Delivery',
    originType: 'FAILED DELIVERY',
    status: 'PENDING ACTION',
    itemCount: 1,
    refundAmount: '$74.20',
    product: 'Philosophy & Ethics Hardcover Book Set',
    carrier: 'DHL Express',
    trackingNumber: 'DHL-44201-99'
  },
  {
    id: '#RET-8839',
    rawId: 'RET-8839',
    initiated: '2h ago',
    date: '2026-09-21 16:40',
    customer: 'Tahani Al-Jamil',
    email: 'tahani.gala@aljamil.co.uk',
    orderNumber: 'Order #ORD-1150-Z',
    reason: 'Wrong Item Sent',
    originType: 'CUSTOMER REQUESTED',
    status: 'RESTOCKED',
    itemCount: 3,
    refundAmount: '$890.00',
    product: 'Luxury Cashmere Throw & Silk Cushion Set',
    carrier: 'UPS Worldwide',
    trackingNumber: '1Z9999999999999999'
  },
  {
    id: '#RET-8835',
    rawId: 'RET-8835',
    initiated: '5h ago',
    date: '2026-09-21 13:40',
    customer: 'Jason Mendoza',
    email: 'jason.dance@jacksonville.fl',
    orderNumber: 'Order #ORD-0082-A',
    reason: 'Defective Unit',
    originType: 'CUSTOMER REQUESTED',
    status: 'WAREHOUSE RECEIVED',
    itemCount: 1,
    refundAmount: '$120.00',
    product: 'Portable Bluetooth Party Speaker 500W',
    carrier: 'USPS Priority',
    trackingNumber: '9400100000000000000000'
  },
  // Page 2 & 3 mock data
  {
    id: '#RET-8830',
    rawId: 'RET-8830',
    initiated: '8h ago',
    date: '2026-09-21 10:30',
    customer: 'Michael Realman',
    email: 'michael@goodplace.architect',
    orderNumber: 'Order #ORD-0012-M',
    reason: 'Unwanted Gift',
    originType: 'CUSTOMER REQUESTED',
    status: 'REFUNDED',
    itemCount: 1,
    refundAmount: '$315.00',
    product: 'Architectural Drafting Set Deluxe',
    carrier: 'FedEx Ground',
    trackingNumber: 'TRK-44321-US'
  },
  {
    id: '#RET-8828',
    rawId: 'RET-8828',
    initiated: '1d ago',
    date: '2026-09-20 15:12',
    customer: 'Janet Subroutine',
    email: 'janet@void.universe',
    orderNumber: 'Order #ORD-9900-J',
    reason: 'Defective Unit',
    originType: 'CUSTOMER REQUESTED',
    status: 'PENDING ACTION',
    itemCount: 4,
    refundAmount: '$450.00',
    product: 'Multi-Sensor Smart Home Hub v3',
    carrier: 'DHL eCommerce',
    trackingNumber: 'DHL-88771-00'
  },
  {
    id: '#RET-8824',
    rawId: 'RET-8824',
    initiated: '1d ago',
    date: '2026-09-20 11:20',
    customer: 'Simone Garnet',
    email: 'simone@neuro.edu.au',
    orderNumber: 'Order #ORD-7712-S',
    reason: 'Wrong Item Sent',
    originType: 'CUSTOMER REQUESTED',
    status: 'WAREHOUSE RECEIVED',
    itemCount: 1,
    refundAmount: '$95.00',
    product: 'Wireless Ergonomic Trackball Mouse',
    carrier: 'UPS Ground',
    trackingNumber: '1Z8888888888888888'
  },
  {
    id: '#RET-8820',
    rawId: 'RET-8820',
    initiated: '2d ago',
    date: '2026-09-19 14:05',
    customer: 'Doug Forcett',
    email: 'doug@calgary.nature',
    orderNumber: 'Order #ORD-3321-D',
    reason: 'Damaged on arrival',
    originType: 'CUSTOMER REQUESTED',
    status: 'RESTOCKED',
    itemCount: 2,
    refundAmount: '$65.00',
    product: 'Eco-Friendly Hemp Backpack & Flask',
    carrier: 'Canada Post',
    trackingNumber: 'CP-99882-CA'
  }
];

export const ReturnsManagementPage = () => {
  // Ref cho file upload EasyExcel
  const fileInputRef = useRef(null);

  // State
  const [returnsList, setReturnsList] = useState(INITIAL_RETURNS);
  const [selectedFilter, setSelectedFilter] = useState('All Requests');
  const [searchQuery, setSearchQuery] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 4;

  // Modals & notification toast
  const [selectedReturn, setSelectedReturn] = useState(null);
  const [isNewReturnModalOpen, setIsNewReturnModalOpen] = useState(false);
  const [toastMessage, setToastMessage] = useState(null);

  // New return form state
  const [newReturnForm, setNewReturnForm] = useState({
    customer: '',
    orderNumber: '',
    reason: 'Damaged on arrival',
    originType: 'CUSTOMER REQUESTED',
    refundAmount: '',
    product: '',
    carrier: 'FedEx Priority'
  });

  const showToast = (msg) => {
    setToastMessage(msg);
    setTimeout(() => {
      setToastMessage(null);
    }, 3500);
  };

  // Filter & Search Logic
  const filteredReturns = useMemo(() => {
    return returnsList.filter((item) => {
      // Status filter
      let matchesFilter = true;
      if (selectedFilter === 'Pending') {
        matchesFilter = item.status === 'PENDING ACTION';
      } else if (selectedFilter === 'In-Transit') {
        matchesFilter = item.status === 'IN-TRANSIT';
      } else if (selectedFilter === 'Warehouse Received') {
        matchesFilter = item.status === 'WAREHOUSE RECEIVED';
      } else if (selectedFilter === 'Restocked') {
        matchesFilter = item.status === 'RESTOCKED';
      } else if (selectedFilter === 'Refunded') {
        matchesFilter = item.status === 'REFUNDED';
      }

      // Search filter
      const q = searchQuery.toLowerCase().trim();
      const matchesSearch =
        !q ||
        item.id.toLowerCase().includes(q) ||
        item.customer.toLowerCase().includes(q) ||
        item.orderNumber.toLowerCase().includes(q) ||
        item.reason.toLowerCase().includes(q) ||
        item.status.toLowerCase().includes(q);

      return matchesFilter && matchesSearch;
    });
  }, [returnsList, selectedFilter, searchQuery]);

  // Pagination
  const totalEntries = 1284;
  const currentTotal = filteredReturns.length;
  const totalPages = Math.ceil(currentTotal / itemsPerPage) || 1;
  const paginatedReturns = useMemo(() => {
    const start = (currentPage - 1) * itemsPerPage;
    return filteredReturns.slice(start, start + itemsPerPage);
  }, [filteredReturns, currentPage]);

  // Quick Action: Mark Received
  const handleMarkReceived = (returnId) => {
    setReturnsList((prev) =>
      prev.map((item) =>
        item.id === returnId
          ? { ...item, status: 'WAREHOUSE RECEIVED' }
          : item
      )
    );
    showToast(`Return ${returnId} has been marked as WAREHOUSE RECEIVED!`);
    if (selectedReturn && selectedReturn.id === returnId) {
      setSelectedReturn((prev) => ({ ...prev, status: 'WAREHOUSE RECEIVED' }));
    }
  };

  // Quick Action: Create New Return
  const handleCreateReturn = (e) => {
    e.preventDefault();
    if (!newReturnForm.customer || !newReturnForm.orderNumber) {
      alert('Vui lòng nhập tên khách hàng và mã đơn hàng!');
      return;
    }

    const newIdNum = 8843 + returnsList.length;
    const newEntry = {
      id: `#RET-${newIdNum}`,
      rawId: `RET-${newIdNum}`,
      initiated: 'Just now',
      date: new Date().toISOString().slice(0, 16).replace('T', ' '),
      customer: newReturnForm.customer,
      email: `${newReturnForm.customer.toLowerCase().replace(/\s+/g, '.')}@example.com`,
      orderNumber: newReturnForm.orderNumber.startsWith('Order #')
        ? newReturnForm.orderNumber
        : `Order #${newReturnForm.orderNumber}`,
      reason: newReturnForm.reason,
      originType: newReturnForm.originType,
      status: 'PENDING ACTION',
      itemCount: 1,
      refundAmount: newReturnForm.refundAmount ? `$${newReturnForm.refundAmount}` : '$99.00',
      product: newReturnForm.product || 'Standard Consumer Item',
      carrier: newReturnForm.carrier,
      trackingNumber: `TRK-${Math.floor(10000 + Math.random() * 90000)}-VN`
    };

    setReturnsList([newEntry, ...returnsList]);
    setIsNewReturnModalOpen(false);
    setNewReturnForm({
      customer: '',
      orderNumber: '',
      reason: 'Damaged on arrival',
      originType: 'CUSTOMER REQUESTED',
      refundAmount: '',
      product: '',
      carrier: 'FedEx Priority'
    });
    showToast(`Đã tạo yêu cầu trả hàng ${newEntry.id} thành công!`);
  };

  // Export Excel Action (Alibaba EasyExcel Chuẩn Production)
  const handleExportExcel = async () => {
    try {
      showToast('Đang tạo và tải file Excel (.xlsx) bằng Alibaba EasyExcel...');
      const blob = await returnService.exportReturnsExcel(selectedFilter, searchQuery);
      const url = URL.createObjectURL(
        new Blob([blob], {
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
        })
      );
      const link = document.createElement('a');
      link.setAttribute('href', url);
      link.setAttribute('download', `returns_export_${new Date().toISOString().slice(0, 10)}.xlsx`);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      URL.revokeObjectURL(url);
      showToast('Đã xuất file Excel thành công bằng Alibaba EasyExcel!');
    } catch (error) {
      console.error('Lỗi khi xuất file Excel từ EasyExcel API:', error);
      showToast('Không thể kết nối API xuất Excel, vui lòng thử lại!');
    }
  };

  // Import Excel Action (Alibaba EasyExcel Chuẩn Production)
  const handleFileImport = async (e) => {
    const file = e.target.files?.[0];
    if (!file) return;

    try {
      showToast('Đang đọc và xử lý file Excel bằng Alibaba EasyExcel...');
      const res = await returnService.importReturnsExcel(file);
      const { totalRows, successCount, insertedCount, updatedCount, failedCount, errorMessages } = res;

      if (failedCount === 0) {
        showToast(`Nhập thành công ${successCount}/${totalRows} dòng (Tạo mới: ${insertedCount || 0}, Ghi đè: ${updatedCount || 0})!`);
      } else {
        showToast(`Thành công: ${successCount} (Mới: ${insertedCount || 0}, Ghi đè: ${updatedCount || 0}), Lỗi: ${failedCount} dòng.`);
        if (errorMessages && errorMessages.length > 0) {
          alert('Chi tiết một số lỗi khi nhập:\n' + errorMessages.slice(0, 5).join('\n'));
        }
      }
    } catch (error) {
      console.error('Lỗi khi nhập Excel bằng EasyExcel:', error);
      showToast('Lỗi khi nhập file Excel, vui lòng kiểm tra định dạng file!');
    } finally {
      if (fileInputRef.current) {
        fileInputRef.current.value = '';
      }
    }
  };

  // Export CSV Action (Gọi API Backend)
  const handleExportCSV = async () => {
    try {
      showToast('Đang tạo và tải file CSV từ hệ thống...');
      const blob = await returnService.exportReturnsCsv(selectedFilter, searchQuery);
      const url = URL.createObjectURL(new Blob([blob], { type: 'text/csv;charset=utf-8;' }));
      const link = document.createElement('a');
      link.setAttribute('href', url);
      link.setAttribute('download', `returns_export_${new Date().toISOString().slice(0, 10)}.csv`);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      URL.revokeObjectURL(url);
      showToast('Đã xuất file CSV thành công từ máy chủ!');
    } catch (error) {
      console.warn('Lỗi khi tải CSV từ API backend, chuyển sang xuất từ dữ liệu hiện tại:', error);
      // Fallback: xuất từ dữ liệu local
      const headers = ['Return ID,Customer,Order Number,Reason,Origin Type,Status,Refund Amount,Initiated\n'];
      const rows = returnsList.map(
        (r) =>
          `"${r.id}","${r.customer}","${r.orderNumber}","${r.reason}","${r.originType}","${r.status}","${r.refundAmount}","${r.initiated}"`
      );
      const blob = new Blob([headers.concat(rows.join('\n'))], { type: 'text/csv;charset=utf-8;' });
      const url = URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.setAttribute('href', url);
      link.setAttribute('download', `returns_export_${new Date().toISOString().slice(0, 10)}.csv`);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      URL.revokeObjectURL(url);
      showToast('Đã xuất file CSV thành công!');
    }
  };

  // Helpers for badge styles
  const getStatusBadge = (status) => {
    switch (status) {
      case 'IN-TRANSIT':
        return (
          <span className="inline-block px-3 py-1 text-[11px] font-bold tracking-wider rounded-full bg-blue-100/80 text-blue-700">
            IN-TRANSIT
          </span>
        );
      case 'PENDING ACTION':
        return (
          <span className="inline-block px-3 py-1 text-[11px] font-bold tracking-wider rounded-full bg-red-50 text-red-600 border border-red-200/70">
            PENDING ACTION
          </span>
        );
      case 'RESTOCKED':
        return (
          <span className="inline-block px-3.5 py-1 text-[11px] font-bold tracking-wider rounded-full bg-[#065f46] text-white">
            RESTOCKED
          </span>
        );
      case 'WAREHOUSE RECEIVED':
        return (
          <span className="inline-block px-3 py-1 text-[11px] font-bold tracking-wider rounded-full bg-[#e0e7ff] text-[#4338ca]">
            WAREHOUSE RECEIVED
          </span>
        );
      case 'REFUNDED':
        return (
          <span className="inline-block px-3.5 py-1 text-[11px] font-bold tracking-wider rounded-full bg-emerald-100 text-emerald-800">
            REFUNDED
          </span>
        );
      default:
        return (
          <span className="inline-block px-3 py-1 text-[11px] font-bold tracking-wider rounded-full bg-slate-100 text-slate-700">
            {status}
          </span>
        );
    }
  };

  const getOriginBadge = (origin) => {
    if (origin === 'FAILED DELIVERY') {
      return (
        <div className="flex items-center gap-1.5 text-[11px] font-bold tracking-wide text-red-600">
          <ShieldAlert className="w-3.5 h-3.5 text-red-500" />
          <span>FAILED DELIVERY</span>
        </div>
      );
    }
    return (
      <div className="flex items-center gap-1.5 text-[11px] font-bold tracking-wide text-blue-600">
        <User className="w-3.5 h-3.5 text-blue-600" />
        <span>CUSTOMER REQUESTED</span>
      </div>
    );
  };

  return (
    <div className="flex min-h-screen bg-[#f4f6fb] text-slate-800 font-sans antialiased">
      {/* ================= TOAST NOTIFICATION ================= */}
      {toastMessage && (
        <div className="fixed top-5 right-5 z-50 flex items-center gap-2.5 bg-slate-900 text-white px-4 py-3 rounded-lg shadow-xl animate-fade-in text-sm border border-slate-700">
          <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0" />
          <span>{toastMessage}</span>
        </div>
      )}

      {/* ================= LEFT SIDEBAR ================= */}
      <aside className="w-64 bg-white border-r border-slate-200/80 flex flex-col justify-between shrink-0 select-none">
        <div>
          {/* Brand Header */}
          <div className="p-6 border-b border-slate-100/80">
            <div className="flex items-center gap-3">
              <div className="w-9 h-9 rounded-lg bg-[#1a44c2] flex items-center justify-center text-white shadow-sm">
                <Truck className="w-5 h-5" />
              </div>
              <div>
                <h1 className="font-bold text-slate-900 text-[15px] leading-tight">Precision Ops</h1>
                <p className="text-[11px] text-slate-400 font-medium">Admin Console</p>
              </div>
            </div>
            <div className="mt-2 text-[11px] text-slate-400 tracking-tight font-normal">
              Quản lý Trả hàng (Returning)
            </div>
          </div>

          {/* Navigation Items */}
          <nav className="p-4 space-y-1">
            <button
              type="button"
              className="w-full flex items-center gap-3 px-3.5 py-2.5 rounded-lg text-sm font-medium text-slate-600 hover:text-slate-900 hover:bg-slate-50 transition-colors"
            >
              <LayoutGrid className="w-4 h-4 text-slate-500" />
              <span>Overview</span>
            </button>

            {/* Active Item */}
            <button
              type="button"
              className="w-full flex items-center gap-3 px-3.5 py-2.5 rounded-lg text-sm font-semibold bg-[#eef2ff] text-[#1a44c2] transition-colors"
            >
              <RotateCcw className="w-4 h-4 text-[#1a44c2]" />
              <span>Returns Management</span>
            </button>

            <button
              type="button"
              className="w-full flex items-center gap-3 px-3.5 py-2.5 rounded-lg text-sm font-medium text-slate-600 hover:text-slate-900 hover:bg-slate-50 transition-colors"
            >
              <Truck className="w-4 h-4 text-slate-500" />
              <span>Fleet Tracking</span>
            </button>

            <button
              type="button"
              className="w-full flex items-center gap-3 px-3.5 py-2.5 rounded-lg text-sm font-medium text-slate-600 hover:text-slate-900 hover:bg-slate-50 transition-colors"
            >
              <Warehouse className="w-4 h-4 text-slate-500" />
              <span>Warehouse</span>
            </button>

            <button
              type="button"
              className="w-full flex items-center gap-3 px-3.5 py-2.5 rounded-lg text-sm font-medium text-slate-600 hover:text-slate-900 hover:bg-slate-50 transition-colors"
            >
              <BarChart3 className="w-4 h-4 text-slate-500" />
              <span>Analytics</span>
            </button>
          </nav>
        </div>

        {/* Bottom Sidebar */}
        <div className="p-4 space-y-2 border-t border-slate-100">
          {/* New Return Request Button */}
          <button
            type="button"
            onClick={() => setIsNewReturnModalOpen(true)}
            className="w-full py-2.5 px-4 bg-[#1a44c2] hover:bg-[#1537a3] text-white text-xs font-semibold rounded-lg flex items-center justify-center gap-2 shadow-sm transition active:scale-[0.99] cursor-pointer"
          >
            <Plus className="w-3.5 h-3.5 stroke-[2.5]" />
            <span>New Return Request</span>
          </button>

          <button
            type="button"
            className="w-full flex items-center gap-3 px-3 py-2 rounded-lg text-xs font-medium text-slate-600 hover:text-slate-900 hover:bg-slate-50 transition-colors"
          >
            <HelpCircle className="w-4 h-4 text-slate-400" />
            <span>Help Center</span>
          </button>

          <button
            type="button"
            className="w-full flex items-center gap-3 px-3 py-2 rounded-lg text-xs font-medium text-slate-600 hover:text-slate-900 hover:bg-slate-50 transition-colors"
          >
            <LogOut className="w-4 h-4 text-slate-400" />
            <span>Logout</span>
          </button>
        </div>
      </aside>

      {/* ================= MAIN CONTENT AREA ================= */}
      <div className="flex-1 flex flex-col min-w-0 overflow-y-auto">
        {/* Top Header Bar */}
        <header className="bg-white border-b border-slate-200/80 px-8 py-3.5 flex items-center justify-between sticky top-0 z-20">
          {/* Left: Brand + Navigation Tabs */}
          <div className="flex items-center gap-8">
            <span className="font-bold text-slate-900 text-base tracking-tight">Admin Logistics</span>

            <nav className="flex items-center gap-6 text-sm font-medium">
              <button
                type="button"
                className="text-slate-500 hover:text-slate-800 transition-colors cursor-pointer"
              >
                Dashboard
              </button>
              <button
                type="button"
                className="text-slate-500 hover:text-slate-800 transition-colors cursor-pointer"
              >
                Shipments
              </button>
              {/* Active Tab */}
              <button
                type="button"
                className="text-[#1a44c2] font-semibold relative py-1 cursor-pointer"
              >
                Returns
                <span className="absolute bottom-[-14px] left-0 right-0 h-[2px] bg-[#1a44c2] rounded-full"></span>
              </button>
              <button
                type="button"
                className="text-slate-500 hover:text-slate-800 transition-colors cursor-pointer"
              >
                Inventory
              </button>
            </nav>
          </div>

          {/* Right: Search & Actions */}
          <div className="flex items-center gap-4">
            {/* Search Input */}
            <div className="relative">
              <Search className="w-3.5 h-3.5 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => {
                  setSearchQuery(e.target.value);
                  setCurrentPage(1);
                }}
                placeholder="Search Return ID..."
                className="pl-8 pr-3 py-1.5 w-60 text-xs bg-[#f1f5f9]/70 border border-slate-200/70 rounded-md focus:outline-none focus:ring-1 focus:ring-[#1a44c2] focus:bg-white text-slate-700 placeholder:text-slate-400 transition"
              />
            </div>

            {/* Notification Bell */}
            <button
              type="button"
              className="p-1.5 text-slate-500 hover:text-slate-800 hover:bg-slate-100 rounded-full relative transition"
            >
              <Bell className="w-4 h-4" />
              <span className="absolute top-1 right-1 w-1.5 h-1.5 bg-red-500 rounded-full"></span>
            </button>

            {/* Settings */}
            <button
              type="button"
              className="p-1.5 text-slate-500 hover:text-slate-800 hover:bg-slate-100 rounded-full transition"
            >
              <Settings className="w-4 h-4" />
            </button>

            {/* Profile Avatar */}
            <div className="w-7 h-7 rounded-full overflow-hidden border border-slate-300 ring-1 ring-slate-100 cursor-pointer">
              <img
                src="https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80"
                alt="User Avatar"
                className="w-full h-full object-cover"
              />
            </div>
          </div>
        </header>

        {/* Page Main Content */}
        <main className="p-8 space-y-6 max-w-[1400px] w-full mx-auto">
          {/* Header Title & Actions */}
          <div className="flex items-start justify-between">
            <div>
              <h2 className="text-2xl font-bold text-slate-900 tracking-tight">Returns Management</h2>
              <p className="text-xs text-slate-500 mt-1">Manage global reverse logistics and refund workflows.</p>
            </div>

            <div className="flex items-center gap-2">
              {/* Hidden file input for EasyExcel import */}
              <input
                ref={fileInputRef}
                type="file"
                accept=".xlsx, .xls"
                onChange={handleFileImport}
                className="hidden"
              />

              {/* Import Excel Button (EasyExcel) */}
              <button
                type="button"
                onClick={() => fileInputRef.current?.click()}
                className="flex items-center gap-1.5 bg-white border border-slate-200/90 hover:bg-slate-50 text-slate-700 text-xs font-semibold px-3 py-1.5 rounded-md shadow-xs transition active:scale-95 cursor-pointer"
                title="Nhập dữ liệu lớn bằng Alibaba EasyExcel"
              >
                <Upload className="w-3.5 h-3.5 text-blue-600" />
                <span>Import Excel</span>
              </button>

              {/* Export Excel Button (EasyExcel) */}
              <button
                type="button"
                onClick={handleExportExcel}
                className="flex items-center gap-1.5 bg-[#065f46] hover:bg-[#044e39] text-white text-xs font-semibold px-3 py-1.5 rounded-md shadow-xs transition active:scale-95 cursor-pointer"
                title="Xuất file .xlsx chuẩn Production bằng Alibaba EasyExcel"
              >
                <FileSpreadsheet className="w-3.5 h-3.5 text-emerald-300" />
                <span>Export Excel</span>
              </button>

              {/* Export CSV Button */}
              <button
                type="button"
                onClick={handleExportCSV}
                className="flex items-center gap-1.5 bg-white border border-slate-200/90 hover:bg-slate-50 text-slate-700 text-xs font-semibold px-3 py-1.5 rounded-md shadow-xs transition active:scale-95 cursor-pointer"
              >
                <Download className="w-3.5 h-3.5 text-slate-500" />
                <span>Export CSV</span>
              </button>
            </div>
          </div>

          {/* ================= 3 KPI CARDS ================= */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
            {/* Card 1: ACTIVE RETURNS (Solid Royal Blue) */}
            <div className="bg-[#1a44c2] text-white rounded-xl p-5 shadow-sm relative overflow-hidden flex flex-col justify-between min-h-[140px]">
              <div className="flex items-center justify-between">
                <span className="text-[11px] font-bold tracking-wider text-blue-200 uppercase">
                  ACTIVE RETURNS
                </span>
                <CornerUpLeft className="w-4 h-4 text-blue-200" />
              </div>
              <div className="my-1">
                <span className="text-4xl font-bold tracking-tight text-white">1,284</span>
              </div>
              <div className="flex items-center gap-1.5 text-xs text-blue-200 font-medium">
                <TrendingUp className="w-3.5 h-3.5" />
                <span>12% vs last month</span>
              </div>
            </div>

            {/* Card 2: AWAITING INSPECTION (White Card) */}
            <div className="bg-white border border-slate-200/80 rounded-xl p-5 shadow-xs flex flex-col justify-between min-h-[140px]">
              <div className="flex items-center justify-between">
                <span className="text-[11px] font-bold tracking-wider text-slate-500 uppercase">
                  AWAITING INSPECTION
                </span>
                <ClipboardCheck className="w-4 h-4 text-slate-500" />
              </div>
              <div className="my-1">
                <span className="text-4xl font-bold tracking-tight text-slate-900">432</span>
              </div>
              <div className="flex items-center gap-1.5 text-xs text-emerald-600 font-semibold">
                <Zap className="w-3.5 h-3.5 fill-emerald-600 text-emerald-600" />
                <span>24h average cycle</span>
              </div>
            </div>

            {/* Card 3: TOTAL REFUNDS (White Card) */}
            <div className="bg-white border border-slate-200/80 rounded-xl p-5 shadow-xs flex flex-col justify-between min-h-[140px]">
              <div className="flex items-center justify-between">
                <span className="text-[11px] font-bold tracking-wider text-slate-500 uppercase">
                  TOTAL REFUNDS
                </span>
                <Banknote className="w-4 h-4 text-slate-500" />
              </div>
              <div className="my-1">
                <span className="text-4xl font-bold tracking-tight text-slate-900">$142,930.50</span>
              </div>
              <div className="text-xs text-slate-400 font-normal">
                This Fiscal Quarter
              </div>
            </div>
          </div>

          {/* ================= FILTER STATUS BAR ================= */}
          <div className="flex items-center gap-3 pt-2">
            <span className="text-xs font-bold text-slate-500 tracking-wider uppercase whitespace-nowrap">
              FILTER STATUS:
            </span>
            <div className="flex items-center gap-2 overflow-x-auto pb-1">
              {[
                'All Requests',
                'Pending',
                'In-Transit',
                'Warehouse Received',
                'Restocked',
                'Refunded'
              ].map((filterName) => {
                const isActive = selectedFilter === filterName;
                return (
                  <button
                    key={filterName}
                    type="button"
                    onClick={() => {
                      setSelectedFilter(filterName);
                      setCurrentPage(1);
                    }}
                    className={`px-4 py-1.5 rounded-full text-xs font-semibold transition cursor-pointer whitespace-nowrap ${
                      isActive
                        ? 'bg-[#1a44c2] text-white shadow-xs'
                        : 'bg-white hover:bg-slate-50 text-slate-600 border border-slate-200/70'
                    }`}
                  >
                    {filterName}
                  </button>
                );
              })}
            </div>
          </div>

          {/* ================= RETURNS DATA TABLE ================= */}
          <div className="bg-white rounded-xl border border-slate-200/80 shadow-xs overflow-hidden">
            <div className="overflow-x-auto">
              <table className="w-full text-left border-collapse">
                <thead>
                  <tr className="border-b border-slate-100 bg-white text-[11px] font-bold text-slate-400 tracking-wider uppercase">
                    <th className="px-6 py-4">RETURN ID</th>
                    <th className="px-6 py-4">CUSTOMER / ORDER</th>
                    <th className="px-6 py-4">REASON</th>
                    <th className="px-6 py-4">ORIGIN TYPE</th>
                    <th className="px-6 py-4">STATUS</th>
                    <th className="px-6 py-4 text-center">ACTIONS</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {paginatedReturns.length === 0 ? (
                    <tr>
                      <td colSpan={6} className="px-6 py-12 text-center text-slate-400 text-sm">
                        Không tìm thấy yêu cầu trả hàng nào phù hợp với điều kiện tìm kiếm.
                      </td>
                    </tr>
                  ) : (
                    paginatedReturns.map((item) => (
                      <tr key={item.id} className="hover:bg-slate-50/70 transition-colors">
                        {/* Return ID */}
                        <td className="px-6 py-4 align-middle">
                          <div className="font-bold text-[#1a44c2] text-sm">{item.id}</div>
                          <div className="text-[11px] text-slate-400 mt-0.5">
                            Initiated: {item.initiated}
                          </div>
                        </td>

                        {/* Customer / Order */}
                        <td className="px-6 py-4 align-middle">
                          <div className="font-bold text-slate-900 text-sm">{item.customer}</div>
                          <div className="text-[11px] text-slate-400 mt-0.5">{item.orderNumber}</div>
                        </td>

                        {/* Reason */}
                        <td className="px-6 py-4 align-middle">
                          <span className="inline-block bg-[#f1f5f9] text-slate-600 text-xs px-2.5 py-1.5 rounded-md font-normal">
                            {item.reason}
                          </span>
                        </td>

                        {/* Origin Type */}
                        <td className="px-6 py-4 align-middle">
                          {getOriginBadge(item.originType)}
                        </td>

                        {/* Status */}
                        <td className="px-6 py-4 align-middle">
                          {getStatusBadge(item.status)}
                        </td>

                        {/* Actions */}
                        <td className="px-6 py-4 align-middle text-center">
                          <div className="flex items-center justify-center gap-2 text-xs">
                            <button
                              type="button"
                              onClick={() => setSelectedReturn(item)}
                              className="text-[#1a44c2] font-semibold hover:underline cursor-pointer"
                            >
                              View Details
                            </button>
                            <span className="text-slate-300">|</span>
                            <button
                              type="button"
                              onClick={() => handleMarkReceived(item.id)}
                              className="text-slate-600 hover:text-slate-900 cursor-pointer font-medium"
                            >
                              Mark Received
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>

            {/* Table Footer / Pagination */}
            <div className="border-t border-slate-100 px-6 py-3.5 flex items-center justify-between bg-white text-xs">
              <span className="text-slate-500">
                Showing {paginatedReturns.length > 0 ? (currentPage - 1) * itemsPerPage + 1 : 0} to{' '}
                {Math.min(currentPage * itemsPerPage, totalEntries)} of {totalEntries} entries
              </span>

              <div className="flex items-center gap-1.5">
                {/* Prev Button */}
                <button
                  type="button"
                  disabled={currentPage === 1}
                  onClick={() => setCurrentPage((p) => Math.max(1, p - 1))}
                  className="w-7 h-7 flex items-center justify-center rounded border border-slate-200 text-slate-400 hover:bg-slate-50 disabled:opacity-40 disabled:cursor-not-allowed transition"
                >
                  <ChevronLeft className="w-3.5 h-3.5" />
                </button>

                {/* Page 1 */}
                <button
                  type="button"
                  onClick={() => setCurrentPage(1)}
                  className={`w-7 h-7 flex items-center justify-center rounded text-xs font-semibold transition ${
                    currentPage === 1
                      ? 'bg-[#1a44c2] text-white shadow-xs'
                      : 'border border-slate-200 text-slate-600 hover:bg-slate-50'
                  }`}
                >
                  1
                </button>

                {/* Page 2 */}
                <button
                  type="button"
                  onClick={() => setCurrentPage(2)}
                  className={`w-7 h-7 flex items-center justify-center rounded text-xs font-semibold transition ${
                    currentPage === 2
                      ? 'bg-[#1a44c2] text-white shadow-xs'
                      : 'border border-slate-200 text-slate-600 hover:bg-slate-50'
                  }`}
                >
                  2
                </button>

                {/* Page 3 */}
                <button
                  type="button"
                  onClick={() => setCurrentPage(3)}
                  className={`w-7 h-7 flex items-center justify-center rounded text-xs font-semibold transition ${
                    currentPage === 3
                      ? 'bg-[#1a44c2] text-white shadow-xs'
                      : 'border border-slate-200 text-slate-600 hover:bg-slate-50'
                  }`}
                >
                  3
                </button>

                {/* Next Button */}
                <button
                  type="button"
                  disabled={currentPage === 3}
                  onClick={() => setCurrentPage((p) => Math.min(3, p + 1))}
                  className="w-7 h-7 flex items-center justify-center rounded border border-slate-200 text-slate-600 hover:bg-slate-50 disabled:opacity-40 disabled:cursor-not-allowed transition"
                >
                  <ChevronRight className="w-3.5 h-3.5" />
                </button>
              </div>
            </div>
          </div>

          {/* ================= 2 BOTTOM CARDS ================= */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-5 pt-1">
            {/* Left Card: Efficiency Insights */}
            <div className="bg-white rounded-xl border border-slate-200/80 p-5 shadow-xs flex flex-col justify-between">
              <div>
                <div className="flex items-center justify-between mb-4">
                  <div className="flex items-center gap-2">
                    <Sparkles className="w-4 h-4 text-[#2563eb]" />
                    <h3 className="font-bold text-slate-900 text-sm">Efficiency Insights</h3>
                  </div>
                  <div className="text-xs">
                    <span className="text-slate-500">Avg. Processing Time</span>
                    <span className="font-bold text-emerald-600 text-sm ml-2">14.2 Hours</span>
                  </div>
                </div>

                {/* Progress bar */}
                <div className="w-full bg-slate-100 rounded-full h-2 my-4 overflow-hidden">
                  <div className="bg-emerald-500 h-2 rounded-full w-[76%] transition-all duration-500"></div>
                </div>
              </div>

              <p className="text-xs text-slate-500">
                Processing is 12% faster than last week. Great job, Team A!
              </p>
            </div>

            {/* Right Card: Alerts */}
            <div className="bg-white rounded-xl border border-slate-200/80 p-5 shadow-xs">
              <div className="flex items-center gap-1.5 mb-4">
                <AlertTriangle className="w-4 h-4 text-red-600 fill-red-600/10" />
                <h3 className="font-bold text-slate-900 text-sm">Alerts</h3>
              </div>

              <div className="grid grid-cols-2 gap-3.5">
                {/* Urgent Inspection Alert */}
                <div className="bg-[#fef2f2] border border-red-100/90 rounded-lg p-3.5 flex flex-col justify-between">
                  <span className="text-[11px] font-bold text-red-600 tracking-wider uppercase">
                    URGENT INSPECTION
                  </span>
                  <div className="text-2xl font-extrabold text-red-600 my-1">12</div>
                  <span className="text-xs text-slate-500">Exceeding 48h SLA</span>
                </div>

                {/* Carrier Delay Alert */}
                <div className="bg-[#f8fafc] border border-slate-100 rounded-lg p-3.5 flex flex-col justify-between">
                  <span className="text-[11px] font-bold text-slate-500 tracking-wider uppercase">
                    CARRIER DELAY
                  </span>
                  <div className="text-2xl font-extrabold text-slate-800 my-1">08</div>
                  <span className="text-xs text-slate-500">North East Region</span>
                </div>
              </div>
            </div>
          </div>
        </main>
      </div>

      {/* ================= MODAL: VIEW DETAILS ================= */}
      {selectedReturn && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-xs z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl max-w-xl w-full p-6 shadow-2xl border border-slate-200 animate-in fade-in zoom-in-95 duration-200">
            <div className="flex items-start justify-between border-b border-slate-100 pb-4">
              <div>
                <div className="flex items-center gap-2.5">
                  <h3 className="text-lg font-bold text-slate-900">
                    Chi tiết yêu cầu {selectedReturn.id}
                  </h3>
                  {getStatusBadge(selectedReturn.status)}
                </div>
                <p className="text-xs text-slate-500 mt-1">
                  Được khởi tạo lúc: {selectedReturn.date} ({selectedReturn.initiated})
                </p>
              </div>
              <button
                type="button"
                onClick={() => setSelectedReturn(null)}
                className="text-slate-400 hover:text-slate-600 p-1 rounded-md hover:bg-slate-100 transition"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <div className="py-4 space-y-4 text-xs">
              <div className="grid grid-cols-2 gap-4 bg-slate-50 p-3.5 rounded-lg border border-slate-100">
                <div>
                  <span className="text-slate-400 block mb-0.5">Khách hàng</span>
                  <span className="font-semibold text-slate-800 text-sm">
                    {selectedReturn.customer}
                  </span>
                  <span className="text-slate-500 block text-[11px]">
                    {selectedReturn.email}
                  </span>
                </div>
                <div>
                  <span className="text-slate-400 block mb-0.5">Mã đơn hàng</span>
                  <span className="font-semibold text-slate-800 text-sm">
                    {selectedReturn.orderNumber}
                  </span>
                  <span className="text-slate-500 block text-[11px]">
                    Số lượng trả: {selectedReturn.itemCount} sản phẩm
                  </span>
                </div>
              </div>

              <div className="border border-slate-100 rounded-lg p-3.5 space-y-2">
                <div className="flex items-center justify-between">
                  <span className="text-slate-500 font-medium">Sản phẩm trả lại:</span>
                  <span className="font-bold text-slate-800">{selectedReturn.product}</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-slate-500 font-medium">Lý do hoàn trả:</span>
                  <span className="bg-slate-100 px-2 py-0.5 rounded text-slate-700 font-medium">
                    {selectedReturn.reason}
                  </span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-slate-500 font-medium">Đơn vị vận chuyển:</span>
                  <span className="text-slate-700">{selectedReturn.carrier} ({selectedReturn.trackingNumber})</span>
                </div>
                <div className="flex items-center justify-between pt-2 border-t border-slate-100">
                  <span className="text-slate-700 font-bold">Số tiền hoàn (Refund Amount):</span>
                  <span className="text-base font-extrabold text-[#1a44c2]">
                    {selectedReturn.refundAmount}
                  </span>
                </div>
              </div>
            </div>

            <div className="border-t border-slate-100 pt-4 flex items-center justify-end gap-2.5">
              <button
                type="button"
                onClick={() => setSelectedReturn(null)}
                className="px-4 py-2 border border-slate-200 text-slate-600 rounded-lg hover:bg-slate-50 text-xs font-semibold transition cursor-pointer"
              >
                Đóng
              </button>
              {selectedReturn.status !== 'WAREHOUSE RECEIVED' && (
                <button
                  type="button"
                  onClick={() => handleMarkReceived(selectedReturn.id)}
                  className="px-4 py-2 bg-[#1a44c2] hover:bg-[#1537a3] text-white rounded-lg text-xs font-semibold shadow-xs transition cursor-pointer"
                >
                  Mark Received
                </button>
              )}
            </div>
          </div>
        </div>
      )}

      {/* ================= MODAL: NEW RETURN REQUEST ================= */}
      {isNewReturnModalOpen && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-xs z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl max-w-lg w-full p-6 shadow-2xl border border-slate-200 animate-in fade-in zoom-in-95 duration-200">
            <div className="flex items-start justify-between border-b border-slate-100 pb-4">
              <div>
                <h3 className="text-lg font-bold text-slate-900">+ Tạo yêu cầu trả hàng mới</h3>
                <p className="text-xs text-slate-500 mt-0.5">
                  Nhập thông tin đơn hàng và lý do hoàn trả từ khách hàng.
                </p>
              </div>
              <button
                type="button"
                onClick={() => setIsNewReturnModalOpen(false)}
                className="text-slate-400 hover:text-slate-600 p-1 rounded-md hover:bg-slate-100 transition"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleCreateReturn} className="py-4 space-y-3.5 text-xs">
              <div>
                <label className="block font-semibold text-slate-700 mb-1">
                  Tên khách hàng <span className="text-red-500">*</span>
                </label>
                <input
                  type="text"
                  required
                  value={newReturnForm.customer}
                  onChange={(e) =>
                    setNewReturnForm({ ...newReturnForm, customer: e.target.value })
                  }
                  placeholder="Ví dụ: Eleanor Shellstrop"
                  className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:outline-none focus:ring-1 focus:ring-[#1a44c2]"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-semibold text-slate-700 mb-1">
                    Mã đơn hàng <span className="text-red-500">*</span>
                  </label>
                  <input
                    type="text"
                    required
                    value={newReturnForm.orderNumber}
                    onChange={(e) =>
                      setNewReturnForm({ ...newReturnForm, orderNumber: e.target.value })
                    }
                    placeholder="Ví dụ: ORD-9921-X"
                    className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:outline-none focus:ring-1 focus:ring-[#1a44c2]"
                  />
                </div>

                <div>
                  <label className="block font-semibold text-slate-700 mb-1">
                    Số tiền hoàn ($)
                  </label>
                  <input
                    type="number"
                    step="0.01"
                    value={newReturnForm.refundAmount}
                    onChange={(e) =>
                      setNewReturnForm({ ...newReturnForm, refundAmount: e.target.value })
                    }
                    placeholder="Ví dụ: 189.50"
                    className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:outline-none focus:ring-1 focus:ring-[#1a44c2]"
                  />
                </div>
              </div>

              <div>
                <label className="block font-semibold text-slate-700 mb-1">
                  Tên sản phẩm
                </label>
                <input
                  type="text"
                  value={newReturnForm.product}
                  onChange={(e) =>
                    setNewReturnForm({ ...newReturnForm, product: e.target.value })
                  }
                  placeholder="Ví dụ: Ergonomic Executive Chair"
                  className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:outline-none focus:ring-1 focus:ring-[#1a44c2]"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-semibold text-slate-700 mb-1">Lý do hoàn trả</label>
                  <select
                    value={newReturnForm.reason}
                    onChange={(e) =>
                      setNewReturnForm({ ...newReturnForm, reason: e.target.value })
                    }
                    className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:outline-none focus:ring-1 focus:ring-[#1a44c2] bg-white"
                  >
                    <option value="Damaged on arrival">Damaged on arrival</option>
                    <option value="Failed Delivery">Failed Delivery</option>
                    <option value="Wrong Item Sent">Wrong Item Sent</option>
                    <option value="Defective Unit">Defective Unit</option>
                    <option value="Customer Exchange">Customer Exchange</option>
                  </select>
                </div>

                <div>
                  <label className="block font-semibold text-slate-700 mb-1">Loại nguồn (Origin)</label>
                  <select
                    value={newReturnForm.originType}
                    onChange={(e) =>
                      setNewReturnForm({ ...newReturnForm, originType: e.target.value })
                    }
                    className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:outline-none focus:ring-1 focus:ring-[#1a44c2] bg-white"
                  >
                    <option value="CUSTOMER REQUESTED">CUSTOMER REQUESTED</option>
                    <option value="FAILED DELIVERY">FAILED DELIVERY</option>
                  </select>
                </div>
              </div>

              <div className="border-t border-slate-100 pt-4 flex items-center justify-end gap-2.5">
                <button
                  type="button"
                  onClick={() => setIsNewReturnModalOpen(false)}
                  className="px-4 py-2 border border-slate-200 text-slate-600 rounded-lg hover:bg-slate-50 text-xs font-semibold transition cursor-pointer"
                >
                  Hủy
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-[#1a44c2] hover:bg-[#1537a3] text-white rounded-lg text-xs font-semibold shadow-xs transition cursor-pointer"
                >
                  Tạo yêu cầu
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default ReturnsManagementPage;
