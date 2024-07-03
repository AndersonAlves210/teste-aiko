package com.example.teste

import adapter.LinhaAdapter
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import data.Linhas
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import util.ApiService
import util.OlhoVivoApi

class MainActivity : AppCompatActivity() {
    private lateinit var searchEditText: EditText
    private lateinit var linhasRecyclerView: RecyclerView
    private lateinit var linhaAdapter: LinhaAdapter
    private var linhasList: MutableList<Linhas> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.setSoftInputMode(SOFT_INPUT_STATE_HIDDEN)

        searchEditText = findViewById(R.id.searchEditText)
        linhasRecyclerView = findViewById(R.id.recyclerView)
        val showMapButton = findViewById<Button>(R.id.mapButton)
        val previsaoButton = findViewById<Button>(R.id.previsaoButton)

        linhaAdapter = LinhaAdapter(linhasList)
        linhasRecyclerView.layoutManager = LinearLayoutManager(this)
        linhasRecyclerView.adapter = linhaAdapter

        val apiService = ApiService.create()

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchTerm = s.toString().trim()
                if (searchTerm.isNotEmpty()) {
                    searchLinhas(apiService, searchTerm)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        showMapButton.setOnClickListener {
            val intent = Intent(this, MapaActivity::class.java)
            startActivity(intent)
        }


        previsaoButton.setOnClickListener {
            val intent = Intent(this, PrevisaoActivity::class.java)
            startActivity(intent)
        }
    }


    private fun searchLinhas(apiService: OlhoVivoApi, searchTerm: String) {
        apiService.getLinhas(searchTerm).enqueue(object : Callback<List<Linhas>> {
            override fun onResponse(call: Call<List<Linhas>>, response: Response<List<Linhas>>) {
                if (response.isSuccessful) {
                    val linhas = response.body()
                    if (linhas != null) {
                        linhasList.clear()
                        linhasList.addAll(linhas)
                        linhaAdapter.notifyDataSetChanged()
                    } else {
                        Log.e("Linhas", "Resposta vazia ou nula")
                    }
                } else {
                    Log.e("Linhas", "Erro na requisição: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Linhas>>, t: Throwable) {
                Log.e("Linhas", "Falha na requisição: ${t.message}")
            }
        })
    }


}