package com.jungcode.jm2.jm2.function;

import android.graphics.drawable.Drawable;

import java.text.Collator;
import java.util.Comparator;

/**
 * Created by User on 2016-01-18.
 */
public class ListData {

    public Drawable mIcon;
    public Drawable downcheck;

    public String mTitle;
    public String mDate;

    public String mTitle2;
    public String mcheck;
    public String count;
    public String count2;

    public static final Comparator<ListData> ALPHA_COMPARATOR = new Comparator<ListData>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(ListData mListDate_1, ListData mListDate_2) {
            return sCollator.compare(mListDate_1.mTitle, mListDate_2.mTitle);
        }
    };


}
