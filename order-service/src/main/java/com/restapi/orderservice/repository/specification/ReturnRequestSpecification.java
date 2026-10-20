package com.restapi.orderservice.repository.specification;

import com.restapi.orderservice.entity.ReturnRequest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ReturnRequestSpecification {

    public static Specification<ReturnRequest> getFilterSpec(String status, String search) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Chỉ lấy các bản ghi chưa bị xóa mềm (soft delete)
            predicates.add(cb.isFalse(root.get("deleted")));

            // 2. Lọc theo trạng thái nếu có
            if (status != null && !status.trim().isEmpty()
                    && !status.equalsIgnoreCase("All Requests")
                    && !status.equalsIgnoreCase("All")) {
                String normStatus = status.trim().replace("-", " ").replace("_", " ");
                predicates.add(cb.equal(
                        cb.upper(root.get("status")),
                        normStatus.toUpperCase()
                ));
            }

            // 3. Tìm kiếm theo từ khóa nếu có (tìm trên nhiều cột indexed)
            if (search != null && !search.trim().isEmpty()) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                Predicate searchPredicate = cb.or(
                        cb.like(cb.lower(root.get("id")), pattern),
                        cb.like(cb.lower(root.get("customerName")), pattern),
                        cb.like(cb.lower(root.get("customerEmail")), pattern),
                        cb.like(cb.lower(root.get("orderNumber")), pattern),
                        cb.like(cb.lower(root.get("productName")), pattern),
                        cb.like(cb.lower(root.get("reason")), pattern),
                        cb.like(cb.lower(root.get("trackingNumber")), pattern)
                );
                predicates.add(searchPredicate);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
