package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.BorrowRecord;

import java.util.List;

public interface BorrowRecordService extends IService<BorrowRecord> {
    List<BorrowRecord> getAllRecords();
    List<BorrowRecord> getMyRecords(Long userId);
    void returnBook(Long recordId);
}
