package com.rkmobiles.app
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class AddRepairActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_repair)
        val customer=findViewById<EditText>(R.id.etCustomer); val phone=findViewById<EditText>(R.id.etPhone)
        val model=findViewById<EditText>(R.id.etModel); val type=findViewById<EditText>(R.id.etType)
        val total=findViewById<EditText>(R.id.etTotal); val paid=findViewById<EditText>(R.id.etPaid)
        val expense=findViewById<EditText>(R.id.etExpense); val calc=findViewById<TextView>(R.id.tvCalc)
        fun n(e:EditText)=e.text.toString().toDoubleOrNull()?:0.0
        fun update(){ calc.text="Pending: ₹${(n(total)-n(paid)).coerceAtLeast(0.0).toInt()}   Profit: ₹${(n(total)-n(expense)).toInt()}" }
        listOf(total,paid,expense).forEach { it.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s:Editable?){update()}; override fun beforeTextChanged(s:CharSequence?,a:Int,b:Int,c:Int){}; override fun onTextChanged(s:CharSequence?,a:Int,b:Int,c:Int){}
        }) }
        findViewById<Button>(R.id.btnSave).setOnClickListener {
            if(customer.text.isBlank() || total.text.isBlank()){Toast.makeText(this,"Enter Customer and Total Amount",Toast.LENGTH_SHORT).show();return@setOnClickListener}
            val t=n(total); val p=n(paid).coerceAtMost(t); val e=n(expense); val pending=(t-p).coerceAtLeast(0.0)
            DbHelper(this).writableDatabase.execSQL("INSERT INTO repairs(customer,phone,model,type,total,paid,pending,expense,date) VALUES(?,?,?,?,?,?,?,?,date('now'))",
                arrayOf(customer.text.toString(),phone.text.toString(),model.text.toString(),type.text.toString(),t,p,pending,e))
            Toast.makeText(this,"Repair Saved",Toast.LENGTH_SHORT).show(); finish()
        }
    }
}
