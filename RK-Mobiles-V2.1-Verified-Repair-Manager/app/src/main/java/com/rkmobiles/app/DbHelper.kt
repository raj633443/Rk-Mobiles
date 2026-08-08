package com.rkmobiles.app

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(context: Context) : SQLiteOpenHelper(context, "rk_mobiles.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE stock(id INTEGER PRIMARY KEY AUTOINCREMENT, model TEXT NOT NULL, qty INTEGER NOT NULL, buy REAL NOT NULL, sell REAL NOT NULL)")
        db.execSQL("CREATE TABLE repairs(id INTEGER PRIMARY KEY AUTOINCREMENT, customer TEXT NOT NULL, phone TEXT, model TEXT, issue TEXT, cost REAL NOT NULL, charge REAL NOT NULL, status TEXT NOT NULL, created INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE pending(id INTEGER PRIMARY KEY AUTOINCREMENT, customer TEXT NOT NULL, phone TEXT, amount REAL NOT NULL, dueDate TEXT, paid INTEGER NOT NULL DEFAULT 0)")
        db.execSQL("CREATE TABLE sales(id INTEGER PRIMARY KEY AUTOINCREMENT, customer TEXT, description TEXT, amount REAL NOT NULL, cost REAL NOT NULL, created INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE expenses(id INTEGER PRIMARY KEY AUTOINCREMENT, description TEXT NOT NULL, amount REAL NOT NULL, created INTEGER NOT NULL)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS stock")
        db.execSQL("DROP TABLE IF EXISTS repairs")
        db.execSQL("DROP TABLE IF EXISTS pending")
        db.execSQL("DROP TABLE IF EXISTS sales")
        db.execSQL("DROP TABLE IF EXISTS expenses")
        onCreate(db)
    }

    fun insertStock(model: String, qty: Int, buy: Double, sell: Double) {
        val v = ContentValues().apply { put("model", model); put("qty", qty); put("buy", buy); put("sell", sell) }
        writableDatabase.insert("stock", null, v)
    }

    fun insertRepair(customer: String, phone: String, model: String, issue: String, cost: Double, charge: Double) {
        val v = ContentValues().apply {
            put("customer", customer); put("phone", phone); put("model", model); put("issue", issue)
            put("cost", cost); put("charge", charge); put("status", "Pending"); put("created", System.currentTimeMillis())
        }
        writableDatabase.insert("repairs", null, v)
    }

    fun insertPending(customer: String, phone: String, amount: Double, dueDate: String) {
        val v = ContentValues().apply { put("customer", customer); put("phone", phone); put("amount", amount); put("dueDate", dueDate); put("paid", 0) }
        writableDatabase.insert("pending", null, v)
    }

    fun markPendingPaid(id: Long) {
        writableDatabase.update("pending", ContentValues().apply { put("paid", 1) }, "id=?", arrayOf(id.toString()))
    }

    fun insertSale(customer: String, description: String, amount: Double, cost: Double) {
        val v = ContentValues().apply { put("customer", customer); put("description", description); put("amount", amount); put("cost", cost); put("created", System.currentTimeMillis()) }
        writableDatabase.insert("sales", null, v)
    }

    fun insertExpense(description: String, amount: Double) {
        val v = ContentValues().apply { put("description", description); put("amount", amount); put("created", System.currentTimeMillis()) }
        writableDatabase.insert("expenses", null, v)
    }

    fun stockCount(): Int = readableDatabase.rawQuery("SELECT COALESCE(SUM(qty),0) FROM stock", null).use { if (it.moveToFirst()) it.getInt(0) else 0 }
    fun pendingTotal(): Double = readableDatabase.rawQuery("SELECT COALESCE(SUM(amount),0) FROM pending WHERE paid=0", null).use { if (it.moveToFirst()) it.getDouble(0) else 0.0 }
    fun salesTotal(): Double = readableDatabase.rawQuery("SELECT COALESCE(SUM(amount),0) FROM sales", null).use { if (it.moveToFirst()) it.getDouble(0) else 0.0 }
    fun costTotal(): Double = readableDatabase.rawQuery("SELECT COALESCE(SUM(cost),0) FROM sales", null).use { if (it.moveToFirst()) it.getDouble(0) else 0.0 }
    fun expenseTotal(): Double = readableDatabase.rawQuery("SELECT COALESCE(SUM(amount),0) FROM expenses", null).use { if (it.moveToFirst()) it.getDouble(0) else 0.0 }
    fun repairProfit(): Double = readableDatabase.rawQuery("SELECT COALESCE(SUM(charge-cost),0) FROM repairs", null).use { if (it.moveToFirst()) it.getDouble(0) else 0.0 }

    fun pendingRows(): List<Array<String>> {
        val out = mutableListOf<Array<String>>()
        readableDatabase.rawQuery("SELECT id,customer,phone,amount,dueDate FROM pending WHERE paid=0 ORDER BY id DESC", null).use { c ->
            while (c.moveToNext()) out.add(arrayOf(c.getString(0), c.getString(1), c.getString(2) ?: "", String.format("%.2f", c.getDouble(3)), c.getString(4) ?: ""))
        }
        return out
    }

    fun repairRows(): List<Array<String>> {
        val out = mutableListOf<Array<String>>()
        readableDatabase.rawQuery("SELECT id,customer,phone,model,issue,cost,charge,status,created FROM repairs ORDER BY id DESC", null).use { c ->
            while (c.moveToNext()) out.add(arrayOf(
                c.getString(0), c.getString(1), c.getString(2) ?: "", c.getString(3) ?: "",
                c.getString(4) ?: "", String.format("%.2f", c.getDouble(5)), String.format("%.2f", c.getDouble(6)),
                c.getString(7), c.getString(8)
            ))
        }
        return out
    }

    fun updateRepairStatus(id: Long, status: String) {
        writableDatabase.update("repairs", ContentValues().apply { put("status", status) }, "id=?", arrayOf(id.toString()))
    }
}
