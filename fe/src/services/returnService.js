import axiosClient from '../api/axiosClient';

export const returnService = {
  /**
   * Gọi API xuất file Excel (.xlsx) chuẩn Production bằng Alibaba EasyExcel
   * @param {string} status - Bộ lọc trạng thái
   * @param {string} search - Từ khóa tìm kiếm
   * @returns {Promise<Blob>} Dữ liệu file blob Excel từ server
   */
  exportReturnsExcel: async (status, search) => {
    const params = {};
    if (status && status !== 'All Requests' && status !== 'All') {
      params.status = status;
    }
    if (search && search.trim() !== '') {
      params.search = search.trim();
    }

    return await axiosClient.get('/returns/export/excel', {
      params,
      responseType: 'blob',
    });
  },

  /**
   * Gọi API xuất file CSV danh sách yêu cầu trả hàng
   */
  exportReturnsCsv: async (status, search) => {
    const params = {};
    if (status && status !== 'All Requests' && status !== 'All') {
      params.status = status;
    }
    if (search && search.trim() !== '') {
      params.search = search.trim();
    }

    return await axiosClient.get('/returns/export', {
      params,
      responseType: 'blob',
    });
  },

  /**
   * Gọi API nhập dữ liệu lớn từ file Excel bằng Alibaba EasyExcel
   * @param {File} file - File Excel được chọn (.xlsx / .xls)
   * @returns {Promise<Object>} Thống kê số lượng nhập thành công / thất bại
   */
  importReturnsExcel: async (file) => {
    const formData = new FormData();
    formData.append('file', file);

    return await axiosClient.post('/returns/import/excel', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  },
};
