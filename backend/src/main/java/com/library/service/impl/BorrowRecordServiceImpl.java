package com.library.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.Book;
import com.library.entity.BorrowRecord;
import com.library.mapper.BookMapper;
import com.library.mapper.BorrowRecordMapper;
import com.library.service.BorrowRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class BorrowRecordServiceImpl extends ServiceImpl<BorrowRecordMapper, BorrowRecord> implements BorrowRecordService {

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    @Autowired
    private BookMapper bookMapper;

    @Override
    public List<BorrowRecord> getAllRecords() {
        return borrowRecordMapper.selectRecordList();
    }

    @Override
    public List<BorrowRecord> getMyRecords(Long userId) {
        return borrowRecordMapper.selectRecordListByUserId(userId);
    }

    @Override
    @Transactional
    public void returnBook(Long recordId) {
        BorrowRecord record = borrowRecordMapper.selectById(recordId);
        if (record == null) {
            throw new IllegalArgumentException("借阅记录不存在");
        }
        if (record.getStatus() == 2) {
            throw new IllegalArgumentException("该图书已归还");
        }

        // 更新记录
        record.setActualReturnTime(LocalDateTime.now());
        record.setStatus(2);
        borrowRecordMapper.updateById(record);

        // 更新图书库存
        Book book = bookMapper.selectById(record.getBookId());
        if (book != null) {
            book.setStock(book.getStock() + 1);
            if (book.getStatus() == 0 && book.getStock() > 0) {
                book.setStatus(1);
            }
            bookMapper.updateById(book);
        }

        log.info("用户 [{}] 归还了图书 [{}]", record.getUserId(), record.getBookId());
    }
}
