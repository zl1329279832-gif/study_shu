package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.Book;
import com.library.entity.BorrowRecord;
import com.library.mapper.BookMapper;
import com.library.mapper.BorrowRecordMapper;
import com.library.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    @Override
    public List<Book> getBookList() {
        return bookMapper.selectBookList();
    }

    @Override
    @Transactional
    public void borrowBook(Long bookId, Long userId) {
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new IllegalArgumentException("图书不存在");
        }
        if (book.getStock() <= 0) {
            throw new IllegalArgumentException("库存不足");
        }
        if (book.getStatus() != 1) {
            throw new IllegalArgumentException("该图书不可借阅");
        }

        // 更新库存
        book.setStock(book.getStock() - 1);
        if (book.getStock() <= 0) {
            book.setStatus(0);
        }
        bookMapper.updateById(book);

        // 创建借阅记录
        BorrowRecord record = new BorrowRecord();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setBorrowTime(LocalDateTime.now());
        record.setExpectReturnTime(LocalDateTime.now().plusDays(30));
        record.setStatus(1);
        borrowRecordMapper.insert(record);

        log.info("用户 [{}] 借阅了图书 [{}]", userId, bookId);
    }
}
