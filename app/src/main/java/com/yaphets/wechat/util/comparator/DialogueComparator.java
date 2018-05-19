package com.yaphets.wechat.util.comparator;

import com.yaphets.wechat.database.entity.Dialogue;

import java.util.Comparator;

public class DialogueComparator implements Comparator<Dialogue> {


    @Override
    public int compare(Dialogue element1, Dialogue element2) {
        return (int) (element2.getLastTimestamp() - element1.getLastTimestamp());
    }
}
