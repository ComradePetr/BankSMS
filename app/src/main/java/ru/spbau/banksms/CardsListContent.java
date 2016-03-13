package ru.spbau.banksms;

import java.util.ArrayList;
import java.util.List;


public class CardsListContent {
    public static List<CardsListItem> items;

    public static List<CardsListItem> items(List<Card> cardsList, int chosenCardId) {
        ArrayList<CardsListItem> list = new ArrayList<>();
        for (Card card : cardsList) {
            list.add(createItem(card, card.id == chosenCardId));
        }
        items = list;
        return list;
    }

    private static CardsListItem createItem(Card card, boolean chosen) {
        return new CardsListItem(card.id, "Card #" + card.number + (chosen ? " (chosen)" : ""));
    }

    public static class CardsListItem {
        public int id;
        public String content;

        public CardsListItem(int id, String content) {
            this.id = id;
            this.content = content;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
