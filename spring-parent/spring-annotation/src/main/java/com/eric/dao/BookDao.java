package com.eric.dao;

import org.springframework.stereotype.Repository;

/**
 * Description: spring-parent
 *
 * @author zhangxiusen
 * @date 2021-1-18
 */
@Repository
public class BookDao {

    private String flag = "1";

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
