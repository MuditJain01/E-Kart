package com.example.ekart.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ekart.databinding.ActivityAddressBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddressBinding
    private lateinit var preferences: SharedPreferences

    private lateinit var totalCost: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = this.getSharedPreferences("user", MODE_PRIVATE)
        loadUserInput()

        totalCost = intent.getStringExtra("totalCost")!!
        binding.proceed.setOnClickListener {
            validateData(
                binding.userName.text.toString(),
                binding.userNumber.text.toString(),
                binding.pinCode.text.toString(),
                binding.state.text.toString(),
                binding.city.text.toString(),
                binding.village.text.toString()
            )
        }
    }

    private fun validateData(
        name: String,
        number: String,
        pinCode: String,
        state: String,
        city: String,
        village: String
    ) {
        if(name.isEmpty() || number.isEmpty() || pinCode.isEmpty() || state.isEmpty() || city.isEmpty() || village.isEmpty()){
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
        }else{
            storeData(pinCode,city,state,village)
        }
    }

    private fun storeData(pinCode: String, city: String, state: String, village: String) {
        val map = hashMapOf<String,Any>()
        map["village"] = village
        map["state"] = state
        map["city"] = city
        map["pinCode"] = pinCode
        Firebase.firestore.collection("users")
            .document(preferences.getString("number","")!!)
            .update(map).addOnSuccessListener {
                val b = Bundle()
                b.putStringArrayList("productIds",intent.getStringArrayListExtra("productIds"))
                b.putString("totalCost",totalCost)
                val intent = Intent(this,CheckoutActivity::class.java)
                intent.putExtras(b)
                startActivity(intent)
            }.addOnFailureListener {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadUserInput() {

        Firebase.firestore.collection("users")
            .document(preferences.getString("number","")!!)
            .get().addOnSuccessListener {
                binding.userName.setText(it.getString("userName"))
                binding.userNumber.setText(it.getString("userPhoneNumber"))
                binding.village.setText(it.getString("village"))
                binding.city.setText(it.getString("city"))
                binding.pinCode.setText(it.getString("pinCode"))
                binding.state.setText(it.getString("state"))
            }
    }
}