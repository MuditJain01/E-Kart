package com.example.ekart.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ekart.MainActivity
import com.example.ekart.databinding.ActivityCheckoutBinding
import com.example.ekart.roomdb.AppDatabase
import com.example.ekart.roomdb.ProductModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject


class CheckoutActivity : AppCompatActivity(), PaymentResultListener{
    private lateinit var binding: ActivityCheckoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val checkout = Checkout()
        checkout.setKeyID("rzp_test_HsHnBpTrndOvpy");

        val price = intent.getStringExtra("totalCost")
        try {
            val options = JSONObject()
            options.put("name", "EKART")
            options.put("description", "Best Ecommerce App")
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg")
            options.put("theme.color", "#9A4DFA")
            options.put("currency", "INR")
            options.put("amount", (price!!.toInt()*100)) //pass amount in currency subunits
            options.put("prefill.email", "muditjain523@gmail.com")
            options.put("prefill.contact", "9876543210")
            checkout.open(this, options)
        } catch (e: Exception) {
            Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPaymentSuccess(p0: String?) {
        Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show()
        uploadData()
    }

    private fun uploadData() {
        val ids = intent.getStringArrayListExtra("productIds")
        for(currId in ids!!){
            fetchData(currId)
        }
    }

    private fun fetchData(productId: String?) {

        val dao = AppDatabase.getInstance(this).productDao()

        Firebase.firestore.collection("products")
            .document(productId!!).get().addOnSuccessListener {

                lifecycleScope.launch(Dispatchers.IO) {
                    dao.deleteProduct(ProductModel(productId))
                }

                saveData(it.getString("productName"),
                it.getString("productSp"),
                productId)
            }
    }

    private fun saveData(productName: String?, productSp: String?, productId: String) {
        val data = hashMapOf<String,Any>()
        val preferences = this.getSharedPreferences("user", MODE_PRIVATE)
        data["name"] = productName!!
        data["price"] = productSp!!
        data["productId"] = productId
        data["status"] = "Ordered"
        data["userId"] = preferences.getString("number","")!!

        val firestore = Firebase.firestore.collection("allOrders")
        val key = firestore.document().id
        data["orderId"] = key

        firestore.document(key).set(data).addOnSuccessListener {
            Toast.makeText(this, "Order Placed", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show()
    }
}