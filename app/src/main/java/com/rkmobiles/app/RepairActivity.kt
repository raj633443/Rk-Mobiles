package com.rkmobiles.app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class RepairActivity:AppCompatActivity(){
 override fun onCreate(b:Bundle?){super.onCreate(b);setContentView(R.layout.activity_repair);val db=DbHelper(this)
  findViewById<Button>(R.id.btnSaveRepair).setOnClickListener{
   val c=findViewById<EditText>(R.id.etRepairCustomer).text.toString().trim()
   val p=findViewById<EditText>(R.id.etRepairPhone).text.toString().trim()
   val m=findViewById<EditText>(R.id.etRepairModel).text.toString().trim()
   val issue=findViewById<EditText>(R.id.etRepairIssue).text.toString().trim()
   val cost=findViewById<EditText>(R.id.etRepairCost).text.toString().toDoubleOrNull()?:0.0
   val charge=findViewById<EditText>(R.id.etRepairCharge).text.toString().toDoubleOrNull()?:0.0
   if(c.isBlank()||m.isBlank()||issue.isBlank()){toast("Fill customer, model and issue");return@setOnClickListener}
   db.insertRepair(c,p,m,issue,cost,charge);toast("Repair job added");render(db)
  };render(db)
 }
 private fun render(db:DbHelper){val box=findViewById<LinearLayout>(R.id.repairList);box.removeAllViews()
 t.setBackgroundResource(R.drawable[...]  // Truncated line
  }
 }
 private fun toast(s:String)=Toast.makeText(this,s,Toast.LENGTH_SHORT).show()
}
