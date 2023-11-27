package com.cursos.api.springsecuritycourse.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class SaveProduct implements Serializable {
    private String name;
    private BigDecimal price;
    private Long categoryId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
