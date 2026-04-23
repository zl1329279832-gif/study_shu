package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BookMapper extends BaseMapper<Book> {

    @Select("SELECT b.*, c.category_name FROM book b " +
            "LEFT JOIN book_category c ON b.category_id = c.id " +
            "WHERE b.deleted = 0 " +
            "ORDER BY b.create_time DESC")
    List<Book> selectBookList();
}
