package com.rkmobiles.app

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(context: Context) : SQLiteOpenHelper(context, "rk_mobiles.db", null, 5) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE stock(id INTEGER PRIMARY KEY AUTOINCREMENT, model TEXT NOT NULL, qty INTEGER NOT NULL, buy REAL NOT NULL, sell REAL NOT NULL, brand TEXT DEFAULT '', imei TEXT DEFAULT '')")
        db.execSQL("CREATE TABLE repairs(id INTEGER PRIMARY KEY AUTOINCREMENT, customer TEXT NOT NULL, phone TEXT, model TEXT, issue TEXT, cost REAL NOT NULL, charge REAL NOT NULL, status TEXT NOT NULL, created INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE pending(id INTEGER PRIMARY KEY AUTOINCREMENT, customer TEXT NOT NULL, phone TEXT, amount REAL NOT NULL, dueDate TEXT, paid INTEGER NOT NULL DEFAULT 0)")
        db.execSQL("CREATE TABLE sales(id INTEGER PRIMARY KEY AUTOINCREMENT, customer TEXT, description TEXT, amount REAL NOT NULL, cost REAL NOT NULL, created INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE expenses(id INTEGER PRIMARY KEY AUTOINCREMENT, description TEXT NOT NULL, amount REAL NOT NULL, created INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE customers(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, phone TEXT UNIQUE, address TEXT DEFAULT '', creditLimit REAL NOT NULL DEFAULT 0)")
        db.execSQL("CREATE TABLE invoices(id INTEGER PRIMARY KEY AUTOINCREMENT, invoiceNo TEXT UNIQUE NOT NULL, customerId INTEGER, customerName TEXT, subtotal REAL NOT NULL, discount REAL NOT NULL DEFAULT 0, total REAL NOT NULL, paid REAL NOT NULL DEFAULT 0, paymentMode TEXT NOT NULL, created INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE invoice_items(id INTEGER PRIMARY KEY AUTOINCREMENT, invoiceId INTEGER NOT NULL, description TEXT NOT NULL, qty INTEGER NOT NULL, rate REAL NOT NULL, cost REAL NOT NULL)")
        db.execSQL("CREATE TABLE purchases(id INTEGER PRIMARY KEY AUTOINCREMENT, supplier TEXT, item TEXT NOT NULL, qty INTEGER NOT NULL, buy REAL NOT NULL, imei TEXT DEFAULT '', created INTEGER NOT NULL)")
        createExtraTables(db)
        createIndexes(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE stock ADD COLUMN brand TEXT DEFAULT ''")
            db.execSQL("ALTER TABLE stock ADD COLUMN imei TEXT DEFAULT ''")
            db.execSQL("CREATE TABLE customers(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, phone TEXT UNIQUE, address TEXT DEFAULT '', creditLimit REAL NOT NULL DEFAULT 0)")
            db.execSQL("CREATE TABLE invoices(id INTEGER PRIMARY KEY AUTOINCREMENT, invoiceNo TEXT UNIQUE NOT NULL, customerId INTEGER, customerName TEXT, subtotal REAL NOT NULL, discount REAL NOT NULL DEFAULT 0, total REAL NOT NULL, paid REAL NOT NULL DEFAULT 0, paymentMode TEXT NOT NULL, created INTEGER NOT NULL)")
            db.execSQL("CREATE TABLE invoice_items(id INTEGER PRIMARY KEY AUTOINCREMENT, invoiceId INTEGER NOT NULL, description TEXT NOT NULL, qty INTEGER NOT NULL, rate REAL NOT NULL, cost REAL NOT NULL)")
        }
        if (oldVersion < 3) {
            db.execSQL("CREATE TABLE purchases(id INTEGER PRIMARY KEY AUTOINCREMENT, supplier TEXT, item TEXT NOT NULL, qty INTEGER NOT NULL, buy REAL NOT NULL, imei TEXT DEFAULT '', created INTEGER NOT NULL)")
        }
        if (oldVersion < 4) {
            createExtraTables(db)
        }

        if (oldVersion < 5) {
            createExtraTables(db)
            createIndexes(db)
        }
    }


    private fun createExtraTables(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS payments(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                customerId INTEGER,
                customerName TEXT NOT NULL,
                amount REAL NOT NULL DEFAULT 0,
                mode TEXT NOT NULL DEFAULT 'Cash',
                note TEXT DEFAULT '',
                created INTEGER NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS stock_moves(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                stockId INTEGER,
                type TEXT NOT NULL,
                qty INTEGER NOT NULL DEFAULT 0,
                reference TEXT DEFAULT '',
                created INTEGER NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS firm_profile(
                id INTEGER PRIMARY KEY CHECK(id=1),
                name TEXT NOT NULL DEFAULT 'RK Mobile',
                phone TEXT DEFAULT '',
                address TEXT DEFAULT '',
                gstin TEXT DEFAULT '',
                invoicePrefix TEXT DEFAULT 'RK'
            )
        """.trimIndent())
    }

    private fun createIndexes(db: SQLiteDatabase) {
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_pending_paid ON pending(paid)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_sales_created ON sales(created)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_expenses_created ON expenses(created)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_repairs_status ON repairs(status)")
    }

    fun insertStock(model: String, qty: Int, buy: Double, sell: Double, brand: String = "", imei: String = "") {
        val v = ContentValues().apply {
            put("model", model); put("qty", qty); put("buy", buy); put("sell", sell)
            put("brand", brand); put("imei", imei)
        }
        writableDatabase.insertOrThrow("stock", null, v)
    }

    fun insertPurchase(supplier: String, item: String, qty: Int, buy: Double, imei: String = "") {
        val v = ContentValues().apply {
            put("supplier", supplier); put("item", item); put("qty", qty)
            put("buy", buy); put("imei", imei); put("created", System.currentTimeMillis())
        }
        writableDatabase.insertOrThrow("purchases", null, v)
    }

    fun searchImei(query: String): List<Array<String>> {
        val out = mutableListOf<Array<String>>()
        readableDatabase.rawQuery(
            "SELECT id,brand,model,imei,qty,buy,sell FROM stock WHERE imei LIKE ? OR model LIKE ? ORDER BY id DESC",
            arrayOf("%$query%", "%$query%")
        ).use { c ->
            while (c.moveToNext()) {
                out.add(arrayOf(
                    c.getString(0), c.getString(1) ?: "", c.getString(2),
                    c.getString(3) ?: "", c.getString(4),
                    String.format("%.2f", c.getDouble(5)),
                    String.format("%.2f", c.getDouble(6))
                ))
            }
        }
        return out
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
        } else writableDatabase.insertOrThrow("customers", null, values)
    }

    fun customerRows(): List<Array<String>> {
        val out = mutableListOf<Array<String>>()
        readableDatabase.rawQuery("SELECT id,name,phone,address,creditLimit FROM customers ORDER BY name COLLATE NOCASE", null).use { c ->
            while (c.moveToNext()) out.add(arrayOf(
                c.getString(0), c.getString(1), c.getString(2) ?: "",
                c.getString(3) ?: "", String.format("%.2f", c.getDouble(4))
            ))
        }
        return out
    }

    fun createInvoice(invoiceNo: String, customerId: Long?, customerName: String, subtotal: Double, discount: Double, paid: Double, paymentMode: String): Long {
        val total = (subtotal - discount).coerceAtLeast(0.0)
        val values = ContentValues().apply {
            put("invoiceNo", invoiceNo)
            if (customerId == null) putNull("customerId") else put("customerId", customerId)
            put("customerName", customerName); put("subtotal", subtotal); put("discount", discount)
            put("total", total); put("paid", paid.coerceIn(0.0, total)); put("paymentMode", paymentMode)
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


    fun savePurchaseAndUpdateStock(
        supplier: String,
        item: String,
        qty: Int,
        buy: Double,
        imei: String = ""
    ) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            insertPurchase(supplier, item, qty, buy, imei)

            val where: String
            val args: Array<String>
            if (imei.isNotBlank()) {
                where = "imei=?"
                args = arrayOf(imei)
            } else {
                where = "model=? AND (imei='' OR imei IS NULL)"
                args = arrayOf(item)
            }

            val stockId = db.query(
                "stock", arrayOf("id", "qty"),
                where, args, null, null, "id DESC", "1"
            ).use { c ->
                if (c.moveToFirst()) {
                    val id = c.getLong(0)
                    val oldQty = c.getInt(1)
                    val values = ContentValues().apply {
                        put("qty", oldQty + qty)
                        put("buy", buy)
                    }
                    db.update("stock", values, "id=?", arrayOf(id.toString()))
                    id
                } else {
                    val values = ContentValues().apply {
                        put("model", item)
                        put("qty", qty)
                        put("buy", buy)
                        put("sell", buy)
                        put("brand", "")
                        put("imei", imei)
                    }
                    db.insertOrThrow("stock", null, values)
                }
            }
            addStockMove(stockId, "PURCHASE", qty, "Purchase")
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun saveInvoiceAndSync(
        invoiceNo: String,
        customerName: String,
        item: String,
        qty: Int,
        rate: Double,
        cost: Double,
        discount: Double,
        paid: Double,
        paymentMode: String
    ): Long {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val subtotal = qty * rate
            val total = (subtotal - discount).coerceAtLeast(0.0)
            val safePaid = paid.coerceIn(0.0, total)

            val invoiceId = createInvoice(
                invoiceNo, null, customerName, subtotal, discount, safePaid, paymentMode
            )
            addInvoiceItem(invoiceId, item, qty, rate, cost)
            insertSale(customerName, item, total, qty * cost)

            val balance = total - safePaid
            if (balance > 0.0) {
                insertPending(customerName, "", balance, "")
            }

            val stock = db.query(
                "stock", arrayOf("id", "qty"),
                "model=? AND qty>=? AND (imei='' OR imei IS NULL)",
                arrayOf(item, qty.toString()), null, null, "id ASC", "1"
            ).use { c ->
                if (c.moveToFirst()) Pair(c.getLong(0), c.getInt(1)) else null
            }

            if (stock != null) {
                val newQty = stock.second - qty
                db.update(
                    "stock",
                    ContentValues().apply { put("qty", newQty) },
                    "id=?",
                    arrayOf(stock.first.toString())
                )
                addStockMove(stock.first, "SALE", qty, invoiceNo)
            }

            db.setTransactionSuccessful()
            return invoiceId
        } finally {
            db.endTransaction()
        }
    }

    fun customerBalance(customerName: String): Double {
        val invoiced = readableDatabase.rawQuery(
            "SELECT COALESCE(SUM(total-paid),0) FROM invoices WHERE customerName=?",
            arrayOf(customerName)
        ).use { if (it.moveToFirst()) it.getDouble(0) else 0.0 }
        val paid = readableDatabase.rawQuery(
            "SELECT COALESCE(SUM(amount),0) FROM payments WHERE customerName=?",
            arrayOf(customerName)
        ).use { if (it.moveToFirst()) it.getDouble(0) else 0.0 }
        return (invoiced - paid).coerceAtLeast(0.0)
    }

    fun stockRows(): List<Array<String>> {
        val out = mutableListOf<Array<String>>()
        readableDatabase.rawQuery(
            "SELECT id,brand,model,qty,buy,sell,imei FROM stock ORDER BY id DESC", null
        ).use { c ->
            while (c.moveToNext()) out.add(arrayOf(
                c.getString(0), c.getString(1) ?: "", c.getString(2),
                c.getString(3), String.format("%.2f", c.getDouble(4)),
                String.format("%.2f", c.getDouble(5)), c.getString(6) ?: ""
            ))
        }
        return out
    }

    fun repairRows(): List<Array<String>> {
        val out = mutableListOf<Array<String>>()
        readableDatabase.rawQuery(
            "SELECT id,customer,phone,model,issue,cost,charge,status FROM repairs ORDER BY id DESC", null
        ).use { c ->
            while (c.moveToNext()) out.add(arrayOf(
                c.getString(0), c.getString(1), c.getString(2) ?: "",
                c.getString(3) ?: "", c.getString(4) ?: "",
                String.format("%.2f", c.getDouble(5)), String.format("%.2f", c.getDouble(6)),
                c.getString(7)
            ))
        }
        return out
    }

    fun expenseRows(): List<Array<String>> {
        val out = mutableListOf<Array<String>>()
        readableDatabase.rawQuery(
            "SELECT id,description,amount,created FROM expenses ORDER BY id DESC", null
        ).use { c ->
            while (c.moveToNext()) out.add(arrayOf(
                c.getString(0), c.getString(1),
                String.format("%.2f", c.getDouble(2)), c.getString(3)
            ))
        }
        return out
    }

    fun updateRepairStatus(id: Long, status: String) {
        writableDatabase.update("repairs", ContentValues().apply { put("status", status) },
            "id=?", arrayOf(id.toString()))
    }

    fun repairCount(status: String = "Pending"): Int =
        readableDatabase.rawQuery("SELECT COUNT(*) FROM repairs WHERE status=?",
            arrayOf(status)).use { if (it.moveToFirst()) it.getInt(0) else 0 }

    fun stockCount(): Int = readableDatabase.rawQuery("SELECT COALESCE(SUM(qty),0) FROM stock", null).use { if (it.moveToFirst()) it.getInt(0) else 0 }
    fun pendingTotal(): Double = readableDatabase.rawQuery("SELECT COALESCE(SUM(amount),0) FROM pending WHERE paid=0", null).use { if (it.moveToFirst()) it.getDouble(0) else 0.0 }
    fun salesTotal(): Double = readableDatabase.rawQuery("SELECT COALESCE(SUM(amount),0) FROM sales", null).use { if (it.moveToFirst()) it.getDouble(0) else 0.0 }
    fun costTotal(): Double = readableDatabase.rawQuery("SELECT COALESCE(SUM(cost),0) FROM sales", null).use { if (it.moveToFirst()) it.getDouble(0) else 0.0 }
    fun expenseTotal(): Double = readableDatabase.rawQuery("SELECT COALESCE(SUM(amount),0) FROM expenses", null).use { if (it.moveToFirst()) it.getDouble(0) else 0.0 }
    fun repairProfit(): Double = readableDatabase.rawQuery("SELECT COALESCE(SUM(charge-cost),0) FROM repairs", null).use { if (it.moveToFirst()) it.getDouble(0) else 0.0 }

    fun pendingRows(): List<Array<String>> {
        val out = mutableListOf<Array<String>>()
        readableDatabase.rawQuery("SELECT id,customer,phone,amount,dueDate FROM pending WHERE paid=0 ORDER BY id DESC", null).use { c ->
            while (c.moveToNext()) out.add(arrayOf(c.getString(0), c.getString(1), c.getString(2) ?: "",
                String.format("%.2f", c.getDouble(3)), c.getString(4) ?: ""))
        }
        return out
    }
    fun invoiceRows(): List<Array<String>> {
        val out = mutableListOf<Array<String>>()
        readableDatabase.rawQuery(
            "SELECT id,invoiceNo,customerName,total,paid,paymentMode,created FROM invoices ORDER BY id DESC", null
        ).use { c ->
            while (c.moveToNext()) out.add(arrayOf(
                c.getString(0), c.getString(1), c.getString(2) ?: "",
                String.format("%.2f", c.getDouble(3)),
                String.format("%.2f", c.getDouble(4)),
                c.getString(5), c.getString(6)
            ))
        }
        return out
    }

    fun customerLedger(customerName: String): List<Array<String>> {
        val out = mutableListOf<Array<String>>()
        readableDatabase.rawQuery(
            """SELECT 'Invoice', invoiceNo, total, paid, created FROM invoices
               WHERE customerName=? ORDER BY created DESC""",
            arrayOf(customerName)
        ).use { c ->
            while (c.moveToNext()) out.add(arrayOf(
                c.getString(0), c.getString(1),
                String.format("%.2f", c.getDouble(2)),
                String.format("%.2f", c.getDouble(3)), c.getString(4)
            ))
        }
        readableDatabase.rawQuery(
            """SELECT 'Payment', note, amount, amount, created FROM payments
               WHERE customerName=? ORDER BY created DESC""",
            arrayOf(customerName)
        ).use { c ->
            while (c.moveToNext()) out.add(arrayOf(
                c.getString(0), c.getString(1),
                String.format("%.2f", c.getDouble(2)),
                String.format("%.2f", c.getDouble(3)), c.getString(4)
            ))
        }
        return out
    }

    fun addPayment(customerId: Long?, customerName: String, amount: Double, mode: String, note: String) {
        val v = ContentValues().apply {
            if (customerId == null) putNull("customerId") else put("customerId", customerId)
            put("customerName", customerName); put("amount", amount)
            put("mode", mode); put("note", note); put("created", System.currentTimeMillis())
        }
        writableDatabase.insertOrThrow("payments", null, v)
    }

    fun addStockMove(stockId: Long?, type: String, qty: Int, reference: String = "") {
        val v = ContentValues().apply {
            if (stockId == null) putNull("stockId") else put("stockId", stockId)
            put("type", type); put("qty", qty); put("reference", reference)
            put("created", System.currentTimeMillis())
        }
        writableDatabase.insertOrThrow("stock_moves", null, v)
    }

    fun lowStockRows(limit: Int = 2): List<Array<String>> {
        val out = mutableListOf<Array<String>>()
        readableDatabase.rawQuery(
            "SELECT id,brand,model,qty,imei FROM stock WHERE qty<=? ORDER BY qty ASC, model",
            arrayOf(limit.toString())
        ).use { c ->
            while (c.moveToNext()) out.add(arrayOf(
                c.getString(0), c.getString(1) ?: "", c.getString(2),
                c.getString(3), c.getString(4) ?: ""
            ))
        }
        return out
    }

    fun setFirmProfile(name: String, phone: String, address: String, gstin: String, prefix: String) {
        val v = ContentValues().apply {
            put("id", 1); put("name", name); put("phone", phone)
            put("address", address); put("gstin", gstin); put("invoicePrefix", prefix)
        }
        writableDatabase.insertWithOnConflict("firm_profile", null, v, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun firmProfile(): Array<String> {
        readableDatabase.rawQuery(
            "SELECT name,phone,address,gstin,invoicePrefix FROM firm_profile WHERE id=1", null
        ).use { c ->
            if (c.moveToFirst()) return arrayOf(
                c.getString(0), c.getString(1) ?: "", c.getString(2) ?: "",
                c.getString(3) ?: "", c.getString(4) ?: "RK"
            )
        }
        return arrayOf("RK Mobile", "", "", "", "RK")
    }

}
