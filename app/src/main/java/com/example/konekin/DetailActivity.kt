package com.example.konekin

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.konekin.databinding.ActivityDetailBinding
import com.example.konekin.model.UserDetail
import com.example.konekin.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID = "extra_id"
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_AGE = "extra_age"
        const val EXTRA_SALARY = "extra_salary"
    }

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        val id = intent.getIntExtra(EXTRA_ID, -1)
        if (id == -1) {
            Toast.makeText(this, "Data tidak ditemukan", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val name = intent.getStringExtra(EXTRA_NAME) ?: "-"
        val age = intent.getIntExtra(EXTRA_AGE, 0)
        val salary = NumberFormat.getNumberInstance(Locale.US)
            .format(intent.getIntExtra(EXTRA_SALARY, 0))

        binding.tvName.text = name
        binding.tvId.text = "ID: $id"
        binding.tvAge.text = "Umur: $age tahun"
        binding.tvSalary.text = "Gaji: $salary"

        binding.progressBar.visibility = View.VISIBLE

        val client = ApiClient.getInstance()
        val response = client.getUserById(id)

        response.enqueue(object : Callback<UserDetail> {
            override fun onResponse(call: Call<UserDetail>, response: Response<UserDetail>) {
                binding.progressBar.visibility = View.GONE

                val body = response.body()
                if (response.isSuccessful && body != null) {
                    val employee = body.data
                    val salary = NumberFormat.getNumberInstance(Locale.US)
                        .format(employee.employeeSalary)

                    binding.tvName.text = employee.employeeName
                    binding.tvId.text = "ID: ${employee.id}"
                    binding.tvAge.text = "Umur: ${employee.employeeAge} tahun"
                    binding.tvSalary.text = "Gaji: $salary"
                }
            }

            override fun onFailure(call: Call<UserDetail>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@DetailActivity, "Koneksi error", Toast.LENGTH_LONG).show()
            }
        })
    }


}