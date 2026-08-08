package com.rkmobiles.app

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(context: Context) : SQLiteOpenHelper(context, "rk_mobiles.db", null, 2) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""CREATE TABLE repairs(
            id INTEGER PRIMARY KEY AUTOINCREMENT, customer TEXT NOT NULL, phone TEXT,
            model TEXT, type TEXT, total REAL NOT NULL, paid REAL NOT NULL,
            pending REAL NOT NULL, expense REAL NOT NULL, date TEXT NOT NULL)""")
        db.execSQL("""CREATE TABLE stock(
            id INTEGER PRIMARY KEY AUTOINCREMENT, item TEXT NOT NULL, qty INTEGER NOT NULL,
            buy REAL NOT NULL, sell REAL NOT NULL)""")
        db.execSQL("""CREATE TABLE expenses(
            id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL,
            amount REAL NOT NULL, date TEXT NOT NULL)""")
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE repairs ADD COLUMN model TEXT")
            db.execSQL("ALTER TABLE repairs ADD COLUMN type TEXT")
        }
    }
}
