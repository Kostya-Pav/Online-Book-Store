package com.onlinebookstore.model;

import java.util.List;

public record SearchParameters(List<String> title, List<String> author, List<String> isbn) {
}
