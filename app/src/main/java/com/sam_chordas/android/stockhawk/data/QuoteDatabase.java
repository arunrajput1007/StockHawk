package com.sam_chordas.android.stockhawk.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by sam_chordas on 10/5/15.
 */
@Database(version = QuoteDatabase.VERSION)
class QuoteDatabase {
    static final int VERSION = 7;
    @Table(QuoteColumns.class)
    static final String QUOTES = "quotes";

    private QuoteDatabase() {
    }
}
