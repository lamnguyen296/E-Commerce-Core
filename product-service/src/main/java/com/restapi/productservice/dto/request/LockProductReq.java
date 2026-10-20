package com.restapi.productservice.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LockProductReq {
    @NotEmpty
    private List<LockProductItem> items;
}
