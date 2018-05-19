package com.yaphets.wechat.ui.view;

import com.ashokvarma.bottomnavigation.TextBadgeItem;

public class HTextBadge extends TextBadgeItem {
    private int mNumber = 0;

    public TextBadgeItem setNumber(int number) {
        mNumber = number;
        setText(String.valueOf(mNumber));
        return this;
    }

    public int getNumber() {
        return mNumber;
    }
}
