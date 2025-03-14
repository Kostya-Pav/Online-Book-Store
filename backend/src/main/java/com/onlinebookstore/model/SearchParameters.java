package com.onlinebookstore.model;

import java.util.List;

public record SearchParameters(List<String> titles, List<String> authors, List<String> isbns) {
}
