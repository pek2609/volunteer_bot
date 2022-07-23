package com.bots.volonteerbot.util;

import com.bots.volonteerbot.persistence.datatable.DataTableRequest;
import com.bots.volonteerbot.persistence.entity.Order;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public final class ToStringUtil {

    private ToStringUtil() {
    }

    public static String getAllOrdersToString(List<Order> orders, DataTableRequest dataTableRequest) {
        if (orders.size() == 0) {
            return "Немає жодного запиту";
        }
        StringBuilder stringList = new StringBuilder();
        stringList.append("Показано ")
                .append(dataTableRequest.getCurrentShowFromEntries())
                .append("-")
                .append(dataTableRequest.getCurrentShowToEntries()).append(" запитів з ")
                .append(dataTableRequest.getAllSize()).append("\n").append("\n");
        int counter = 1;
        for (Order order : orders) {
            stringList.append(counter++).append(". ").append(order.toString()).append("\n");
        }
        return stringList.toString();
    }

    public static InlineKeyboardMarkup getKeyboard(DataTableRequest dataTableRequest) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        if (dataTableRequest.hasPrevious()) {
            buttons.add(InlineKeyboardButton.builder().text("Назад").callbackData(String.valueOf(dataTableRequest.getPage() - 1)).
                    build());
        }
        if (dataTableRequest.hasNext()) {
            buttons.add(InlineKeyboardButton.builder().text("Вперед").callbackData(String.valueOf(dataTableRequest.getPage() + 1)).
                    build());
        }
        return InlineKeyboardMarkup.builder()
                .keyboardRow(buttons)
                .build();
    }
}
