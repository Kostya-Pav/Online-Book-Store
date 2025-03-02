package com.onlinebookstore.model;

import java.math.BigDecimal;

public record SearchParameters(String title, String author, String isbn, BigDecimal price) {
}
