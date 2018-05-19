package com.yaphets.wechat.util.comparator;

import java.text.CollationKey;
import java.text.Collator;
import java.util.Comparator;

public class NicknameComparator  implements Comparator<String> {
    Collator collator = Collator.getInstance();

    @Override
    public int compare(String element1, String element2) {
        CollationKey key1 = collator.getCollationKey(element1.toUpperCase());
        CollationKey key2 = collator.getCollationKey(element2.toUpperCase());
        return key1.compareTo(key2);
    }
}
