package com.eric.service;

import com.eric.dao.BookDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description: spring-parent
 *
 * @author zhangxiusen
 * @date 2021-1-18
 */
@Service
public class BookService {

    @Autowired
    private BookDao bookDao;

    @Override
    public String toString() {
        System.out.println(bookDao.getFlag());
        return "BookService{" +
                "bookDao=" + bookDao +
                '}';
    }
}
