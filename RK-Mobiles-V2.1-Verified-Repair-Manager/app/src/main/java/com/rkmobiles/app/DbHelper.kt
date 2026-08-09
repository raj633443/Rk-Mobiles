package com.rkmobiles.app

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.Locale

class DbHelper(context: Context) :
    SQLiteOpenHelper(context, "rk_mobiles.db", null, DB_VERSION) {

    companion object {
        private const val DB_VERSION = 5
        private const val DB_NAME = "rk_mobiles.db"
    }

    override fun onCreate(db: SQLiteDatabase) {

        // Stock
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS stock(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                model TEXT NOT NULL,
                qty INTEGER NOT NULL DEFAULT 0,
                buy REAL NOT NULL DEFAULT 0,
                sell REAL NOT NULL DEFAULT 0
            )
            """.trimIndent()
        )

        // Repairs
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS repairs(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                customer TEXT NOT NULL,
                phone TEXT,
                model TEXT,
                issue TEXT,
                cost REAL NOT NULL DEFAULT 0,
                charge REAL NOT NULL DEFAULT 0,
                status TEXT NOT NULL DEFAULT 'Pending',
                created INTEGER NOT NULL
            )
            """.trimIndent()
        )

        // Pending payments
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS pending(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                customer TEXT NOT NULL,
                phone TEXT,
                amount REAL NOT NULL DEFAULT 0,
                dueDate TEXT,
                paid INTEGER NOT NULL DEFAULT 0
            )
            """.trimIndent()
        )

        // Sales
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS sales(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                customer TEXT,
                description TEXT,
                amount REAL NOT NULL DEFAULT 0,
                cost REAL NOT NULL DEFAULT 0,
                created INTEGER NOT NULL
            )
            """.trimIndent()
        )

        // Expenses
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS expenses(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                description TEXT NOT NULL,
                amount REAL NOT NULL DEFAULT 0,
                created INTEGER NOT NULL
            )
            """.trimIndent()
        )

        createExtraTables(db)
        createIndexes(db)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {

        /*
         * IMPORTANT:
         * Never DROP existing tables during a normal upgrade.
         * Existing customer/sales/stock data must be preserved.
         */

        if (oldVersion < 2) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS payments(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    customerId INTEGER,
                    customerName TEXT NOT NULL,
                    amount REAL NOT NULL DEFAULT 0,
                    mode TEXT NOT NULL DEFAULT 'Cash',
                    note TEXT DEFAULT '',
                    created INTEGER NOT NULL
                )
                """.trimIndent()
            )
        }

        if (oldVersion < 3) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS stock_moves(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    stockId INTEGER,
                    type TEXT NOT NULL,
                    qty INTEGER NOT NULL DEFAULT 0,
                    reference TEXT DEFAULT '',
                    created INTEGER NOT NULL
                )
                """.trimIndent()
            )
        }

        if (oldVersion < 4) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS firm_profile(
                    id INTEGER PRIMARY KEY CHECK(id = 1),
                    name TEXT NOT NULL DEFAULT 'RK Mobile',
                    phone TEXT DEFAULT '',
                    address TEXT DEFAULT '',
                    gstin TEXT DEFAULT '',
                    invoicePrefix TEXT DEFAULT 'RK'
                )
                """.trimIndent()
            )
        }

        if (oldVersion < 5) {
            // Safety: make sure all additional tables exist.
            createExtraTables(db)
            createIndexes(db)
        }
    }

    private fun createExtraTables(db: SQLiteDatabase) {

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS payments(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                customerId INTEGER,
                customerName TEXT NOT NULL,
                amount REAL NOT NULL DEFAULT 0,
                mode TEXT NOT NULL DEFAULT 'Cash',
                note TEXT DEFAULT '',
                created INTEGER NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS stock_moves(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                stockId INTEGER,
                type TEXT NOT NULL,
                qty INTEGER NOT NULL DEFAULT 0,
                reference TEXT DEFAULT '',
                created INTEGER NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS firm_profile(
                id INTEGER PRIMARY KEY CHECK(id = 1),
                name TEXT NOT NULL DEFAULT 'RK Mobile',
                phone TEXT DEFAULT '',
                address TEXT DEFAULT '',
                gstin TEXT DEFAULT '',
                invoicePrefix TEXT DEFAULT 'RK'
            )
            """.trimIndent()
        )
    }

    private fun createIndexes(db: SQLiteDatabase) {

        db.execSQL(
            "CREATE INDEX IF NOT EXISTS idx_pending_paid ON pending(paid)"
        )

        db.execSQL(
            "CREATE INDEX IF NOT EXISTS idx_sales_created ON sales(created)"
        )

        db.execSQL(
            "CREATE INDEX IF NOT EXISTS idx_expenses_created ON expenses(created)"
        )

        db.execSQL(
            "CREATE INDEX IF NOT EXISTS idx_repairs_status ON repairs(status)"
        )
    }

    // ----------------------------------------------------
    // STOCK
    // ----------------------------------------------------

    fun insertStock(
        model: String,
        qty: Int,
        buy: Double,
        sell: Double
    ) {
        val values = ContentValues().apply {
            put("model", model.trim())
            put("qty", qty)
            put("buy", buy)
            put("sell", sell)
        }

        writableDatabase.insert(
            "stock",
            null,
            values
        )
    }

    // ----------------------------------------------------
    // REPAIR
    // ----------------------------------------------------

    fun insertRepair(
        customer: String,
        phone: String,
        model: String,
        issue: String,
        cost: Double,
        charge: Double
    ) {
        val values = ContentValues().apply {
            put("customer", customer.trim())
            put("phone", phone.trim())
            put("model", model.trim())
            put("issue", issue.trim())
            put("cost", cost)
            put("charge", charge)
            put("status", "Pending")
            put("created", System.currentTimeMillis())
        }

        writableDatabase.insert(
            "repairs",
            null,
            values
        )
    }

    fun updateRepairStatus(
        id: Long,
        status: String
    ) {
        val values = ContentValues().apply {
            put("status", status)
        }

        writableDatabase.update(
            "repairs",
            values,
            "id = ?",
            arrayOf(id.toString())
        )
    }

    // ----------------------------------------------------
    // PENDING
    // ----------------------------------------------------

    fun insertPending(
        customer: String,
        phone: String,
        amount: Double,
        dueDate: String
    ) {
        val values = ContentValues().apply {
            put("customer", customer.trim())
            put("phone", phone.trim())
            put("amount", amount)
            put("dueDate", dueDate.trim())
            put("paid", 0)
        }

        writableDatabase.insert(
            "pending",
            null,
            values
        )
    }

    fun markPendingPaid(id: Long) {
        val values = ContentValues().apply {
            put("paid", 1)
        }

        writableDatabase.update(
            "pending",
            values,
            "id = ?",
            arrayOf(id.toString())
        )
    }

    // ----------------------------------------------------
    // SALES
    // ----------------------------------------------------

    fun insertSale(
        customer: String,
        description: String,
        amount: Double,
        cost: Double
    ) {
        val values = ContentValues().apply {
            put("customer", customer.trim())
            put("description", description.trim())
            put("amount", amount)
            put("cost", cost)
            put("created", System.currentTimeMillis())
        }

        writableDatabase.insert(
            "sales",
            null,
            values
        )
    }

    // ----------------------------------------------------
    // EXPENSE
    // ----------------------------------------------------

    fun insertExpense(
        description: String,
        amount: Double
    ) {
        val values = ContentValues().apply {
            put("description", description.trim())
            put("amount", amount)
            put("created", System.currentTimeMillis())
        }

        writableDatabase.insert(
            "expenses",
            null,
            values
        )
    }

    // ----------------------------------------------------
    // DASHBOARD TOTALS
    // ----------------------------------------------------

    fun stockCount(): Int {
        return readableDatabase.rawQuery(
            "SELECT COALESCE(SUM(qty), 0) FROM stock",
            null
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                cursor.getInt(0)
            } else {
                0
            }
        }
    }

    fun pendingTotal(): Double {
        return readableDatabase.rawQuery(
            "SELECT COALESCE(SUM(amount), 0) FROM pending WHERE paid = 0",
            null
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                cursor.getDouble(0)
            } else {
                0.0
            }
        }
    }

    fun salesTotal(): Double {
        return readableDatabase.rawQuery(
            "SELECT COALESCE(SUM(amount), 0) FROM sales",
            null
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                cursor.getDouble(0)
            } else {
                0.0
            }
        }
    }

    fun costTotal(): Double {
        return readableDatabase.rawQuery(
            "SELECT COALESCE(SUM(cost), 0) FROM sales",
            null
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                cursor.getDouble(0)
            } else {
                0.0
            }
        }
    }

    fun expenseTotal(): Double {
        return readableDatabase.rawQuery(
            "SELECT COALESCE(SUM(amount), 0) FROM expenses",
            null
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                cursor.getDouble(0)
            } else {
                0.0
            }
        }
    }

    fun repairProfit(): Double {
        return readableDatabase.rawQuery(
            """
            SELECT COALESCE(SUM(charge - cost), 0)
            FROM repairs
            """.trimIndent(),
            null
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                cursor.getDouble(0)
            } else {
                0.0
            }
        }
    }

    // ----------------------------------------------------
    // PENDING LIST
    // ----------------------------------------------------

    fun pendingRows(): List<Array<String>> {

        val result = mutableListOf<Array<String>>()

        readableDatabase.rawQuery(
            """
            SELECT id, customer, phone, amount, dueDate
            FROM pending
            WHERE paid = 0
            ORDER BY id DESC
            """.trimIndent(),
            null
        ).use { cursor ->

            while (cursor.moveToNext()) {

                result.add(
                    arrayOf(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2) ?: "",
                        String.format(
                            Locale.US,
                            "%.2f",
                            cursor.getDouble(3)
                        ),
                        cursor.getString(4) ?: ""
                    )
                )
            }
        }

        return result
    }

    // ----------------------------------------------------
    // REPAIR LIST
    // ----------------------------------------------------

    fun repairRows(): List<Array<String>> {

        val result = mutableListOf<Array<String>>()

        readableDatabase.rawQuery(
            """
            SELECT
                id,
                customer,
                phone,
                model,
                issue,
                cost,
                charge,
                status,
                created
            FROM repairs
            ORDER BY id DESC
            """.trimIndent(),
            null
        ).use { cursor ->

            while (cursor.moveToNext()) {

                result.add(
                    arrayOf(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2) ?: "",
                        cursor.getString(3) ?: "",
                        cursor.getString(4) ?: "",
                        String.format(
                            Locale.US,
                            "%.2f",
                            cursor.getDouble(5)
                        ),
                        String.format(
                            Locale.US,
                            "%.2f",
                            cursor.getDouble(6)
                        ),
                        cursor.getString(7),
                        cursor.getString(8)
                    )
                )
            }
        }

        return result
    }
}
