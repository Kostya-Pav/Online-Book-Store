package com.onlinebookstore.model;

import java.util.List;
//Ми не можемо змінити назву поля або використовувати інверсію
// бо вона взаємозв'язана між назвою полів моделі
//і назвами лістів

public record SearchParameters(List<String> title, List<String> author, List<String> isbn) {
}
