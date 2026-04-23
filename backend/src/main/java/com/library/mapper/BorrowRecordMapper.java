package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.BorrowRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BorrowRecordMapper extends BaseMapper<BorrowRecord> {

    @Select("SELECT br.*, u.username, b.book_name, b.author FROM borrow_record br " +
            "LEFT JOIN sys_user u ON br.user_id = u.id " +
            "LEFT JOIN book b ON br.book_id = b.id " +
            "ORDER BY br.create_time DESC")
    List<BorrowRecord> selectRecordList();

    @Select("SELECT br.*, u.username, b.book_name, b.author FROM borrow_record br " +
            "LEFT JOIN sys_user u ON br.user_id = u.id " +
            "LEFT JOIN book b ON br.book_id = b.id " +
            "WHERE br.user_id = #{userId} " +
            "ORDER BY br.create_time DESC")
    List<BorrowRecord> selectRecordListByUserId(@Param("userId") Long userId);
}
