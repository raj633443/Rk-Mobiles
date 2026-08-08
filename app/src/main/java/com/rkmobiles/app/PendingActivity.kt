package com.rkmobiles.app
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class PendingActivity : AppCompatActivity() {
    private val ids=ArrayList<Long>(); private val phones=ArrayList<String>(); private val amounts=ArrayList<Int>()
    override fun onCreate(savedInstanceState: Bundle?){super.onCreate(savedInstanceState);setContentView(R.layout.activity_pending);load()}
    private fun load(){
        ids.clear();phones.clear();amounts.clear()
        val rows=ArrayList<String>(); DbHelper(this).readableDatabase.rawQuery("SELECT id,customer,phone,pending FROM repairs WHERE pending>0 ORDER BY date",null).use{c->
            while(c.moveToNext()){ids.add(c.getLong(0));phones.add(c.getString(2)?:"");amounts.add(c.getDouble(3).toInt());rows.add("${c.getString(1)}    ₹${c.getDouble(3).toInt()}")}
        }
        val list=findViewById<ListView>(R.id.listPending); list.adapter=ArrayAdapter(this,android.R.layout.simple_list_item_1,rows)
        list.setOnItemClickListener{_,_,pos,_-> 
            val phone=phones[pos].filter(Char::isDigit)
            if(phone.length>=10){val msg="Hello,\nYour pending amount is ₹${amounts[pos]}.\nPlease visit RK Mobiles and clear the payment.\nThank you."; startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/91$phone?text="+Uri.encode(msg))))}
            else Toast.makeText(this,"WhatsApp number not available",Toast.LENGTH_SHORT).show()
        }
        list.setOnItemLongClickListener{_,_,pos,_-> 
            val input=EditText(this); input.inputType=2; input.hint="Payment received"
            AlertDialog.Builder(this).setTitle("Receive Payment").setView(input).setPositiveButton("Save"){_,_-> 
                val pay=input.text.toString().toDoubleOrNull()?:0.0
                val db=DbHelper(this).writableDatabase
                db.execSQL("UPDATE repairs SET paid=paid+?, pending=CASE WHEN pending-? < 0 THEN 0 ELSE pending-? END WHERE id=?",arrayOf(pay,pay,pay,ids[pos]));load()
            }.setNegativeButton("Cancel",null).show(); true
        }
    }
}
