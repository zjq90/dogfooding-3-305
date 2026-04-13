package com.library.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 借阅请求DTO
 */
@Data
public class BorrowRequestDTO {
    
    @NotNull(message = "图书ID不能为空")
    private Long bookId;
    
    private Integer borrowDays = 30;
}
