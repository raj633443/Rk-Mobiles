package com.rkmobiles.app

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(context: Context) : SQLiteOpenHelper(context, "rk_mobiles.db", null, 2) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE stock(id INTEGER PRIMARY KEY AUTOINCREMENT, model TEXT NOT NULL, qty INTEGER NOT NULL, buy REAL NOT NULL, sell REAL NOT NULL, brand TEXT DEFAULT '', imei TEXT DEFAULT '')")
        db.execSQL("CREATE TABLE repairs(id INTEGER PRIMARY KEY AUTOINCREMENT, customer TEXT NOT NULL, phone TEXT, model TEXT, issue TEXT, cost REAL NOT NULL, charge REAL NOT NULL, status TEXT NOT NULL, created INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE pending(id INTEGER PRIMARY KEY AUTOINCREMENT, customer TEXT NOT NULL, phone TEXT, amount REAL NOT NULL, dueDate TEXT, paid INTEGER NOT NULL DEFAULT 0)")
        db.execSQL("CREATE TABLE sales(id INTEGER PRIMARY KEY AUTOINCREMENT, customer TEXT, description TEXT, amount REAL NOT NULL, cost REAL NOT NULL, created INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE expenses(id INTEGER PRIMARY KEY AUTOINCREMENT, description TEXT NOT NULL, amount REAL NOT NULL, created INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE customers(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, phone TEXT UNIQUE, address TEXT DEFAULT '', creditLimit REAL NOT NULL DEFAULT 0)")
        db.execSQL("CREATE TABLE invoices(id INTEGER PRIMARY KEY AUTOINCREMENT, invoiceNo TEXT UNIQUE NOT NULL, customerId INTEGER, customerName TEXT, subtotal REAL NOT NULL, discount REAL NOT NULL DEFAULT 0, total REAL NOT NULL, paid REAL NOT NULL DEFAULT 0, paymentMode TEXT NOT NULL, created INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE invoice_items(id INTEGER PRIMARY KEY AUTOINCREMENT, invoiceId INTEGER NOT NULL, description TEXT NOT NULL, qty INTEGER NOT NULL, rate REAL NOT NULL, cost REAL NOT NULL)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE stock ADD COLUMN brand TEXT DEFAULT ''")
            db.execSQL("ALTER TABLE stock ADD COLUMN imei TEXT DEFAULT ''")
            db.execSQL("CREATE TABLE customers(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, phone TEXT UNIQUE, address TEXT DEFAULT '', creditLimit REAL NOT NULL DEFAULT 0)")
            db.execSQL("CREATE TABLE invoices(id INTEGER PRIMARY KEY AUTOINCREMENT, invoiceNo TEXT UNIQUE NOT NULL, customerId INTEGER, customerName TEXT, subtotal REAL NOT NULL, discount REAL NOT NULL DEFAULT 0, total REAL NOT NULL, paid REAL NOT NULL DEFAULT 0, paymentMode TEXT NOT NULL, created INTEGER NOT NULL)")
            db.execSQL("CREATE TABLE invoice_items(id INTEGER PRIMARY KEY AUTOINCREMENT, invoiceId INTEGER NOT NULL, description TEXT NOT NULL, qty INTEGER NOT NULL, rate REAL NOT NULL, cost REAL NOT NULL)")
        }
    }

    fun insertStock(model: String, qty: Int, buy: Double, sell: Double, brand: String = "", imei: String = "") {
        val v = ContentValues().apply {
            put("model", model); put("qty", qty); put("buy", buy); put("sell", sell)
            put("brand", brand); put("imei", imei)
        }
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
        val v = ContentValues().apply {
            put("customer", customer); put("phone", phone); put("amount", amount); put("dueDate", dueDate); put("paid", 0)
        }
        writableDatabase.insert("pending", null, v)
    }

    fun markPendingPaid(id: Long) {
        writableDatabase.update("pending", ContentValues().apply { put("paid", 1) }, "id=?", arrayOf(id.toString()))
    }

    fun insertSale(customer: String, description: String, amount: Double, cost: Double) {
        val v = ContentValues().apply {
            put("customer", customer); put("description", description); put("amount", amount)
            put("cost", cost); put("created", System.currentTimeMillis())
        }
        writableDatabase.insert("sales", null, v)
    }

    fun insertExpense(description: String, amount: Double) {
        val v = ContentValues().apply {
            put("description", description); put("amount", amount); put("created", System.currentTimeMillis())
        }
        writableDatabase.insert("expenses", null, v)
    }

    fun upsertCustomer(name: String, phone: String, address: String, creditLimit: Double): Long {
        val values = ContentValues().apply {
            put("name", name); put("phone", phone); put("address", address); put("creditLimit", creditLimit)
        }
        val existing = readableDatabase.query("customers", arrayOf("id"), "phone=?",
            arrayOf(phone), null, null, null).use { if (it.moveToFirst()) it.getLong(0) else -1L }
        return if (existing >= 0) {
            writableDatabase.update("customers", values, "id=?", arrayOf(existing.toString()))
            existing
        } else {
            writableDatabase.insertOrThrow("customers", null, values)
        }
    }

    fun customerRows(): List<Array<String>> {
        val out = mutableListOf<Array<String>>()
        readableDatabase.rawQuery(
            "SELECT id,name,phone,address,creditLimit FROM customers ORDER BY name COLLATE NOCASE",
            null
        ).use { c ->
            while (c.moveToNext()) {
                out.add(arrayOf(
                    c.getString(0), c.getString(1), c.getString(2) ?: "",
                    c.getString(3) ?: "", String.format("%.2f", c.getDouble(4))
                ))
            }
        }
        return out
    }

    fun createInvoice(
        invoiceNo: String,
        customerId: Long?,
        customerName: String,
        subtotal: Double,
        discount: Double,
        paid: Double,
        paymentMode: String
    ): Long {
        val total = (subtotal - discount).coerceAtLeast(0.0)
        val values = ContentValues().apply {
            put("invoiceNo", invoiceNo)
            if (customerId == null) putNull("customerId") else put("customerId", customerId)
            put("customerName", customerName)
            put("subtotal", subtotal); put("discount", discount); put("total", total)
            put("paid", paid.coerceIn(0.0, total)); put("paymentMode", paymentMode)
            put("created", System.currentTimeMillis())
        }
        return writableDatabase.insertOrThrow("invoices", null, values)
    }

    fun addInvoiceItem(invoiceId: Long, description: String, qty: Int, rate: Double, cost: Double) {
        val values = ContentValues().apply {
            put("invoiceId", invoiceId); put("description", description)
            put("qty", qty); put("rate", rate); put("cost", cost)
        }
        writableDatabase.insertOrThrow("invoice_items", null, values)
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
            while (c.moveToNext()) out.add(arrayOf(
                c.getString(0), c.getString(1), c.getString(2) ?: "",
                String.format("%.2f", c.getDouble(3)), c.getString(4) ?: ""
            ))
        }
        return out
    }
}
