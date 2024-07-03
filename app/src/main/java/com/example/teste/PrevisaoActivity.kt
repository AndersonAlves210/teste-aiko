package com.example.teste

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import util.ApiService
import util.OlhoVivoApi
import adapter.PrevisaoAdapter
import data.Previsao

class PrevisaoActivity : AppCompatActivity() {


    private lateinit var codigoLinhaEditText: EditText
    private lateinit var previsaoRecyclerView: RecyclerView
    private lateinit var previsaoAdapter: PrevisaoAdapter
    private var previsaoList: MutableList<Previsao> = mutableListOf()

    private val apiService: OlhoVivoApi by lazy {
        ApiService.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_previsao)

        codigoLinhaEditText = findViewById(R.id.searchPrevisaoEditText)
        previsaoRecyclerView = findViewById(R.id.recyclerViewPrevisao)

        previsaoAdapter = PrevisaoAdapter(previsaoList)
        previsaoRecyclerView.layoutManager = LinearLayoutManager(this)
        previsaoRecyclerView.adapter = previsaoAdapter

        setupEditTextListeners()
    }

    private fun setupEditTextListeners() {
        codigoLinhaEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val codigoLinha = codigoLinhaEditText.text.toString().trim()
                if (codigoLinha.isNotEmpty()) {
                    fetchPrevisao(codigoLinha)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

    }

    private fun fetchPrevisao(codigoLinha: String) {
        apiService.getPrevisaoPorLinha(codigoLinha)
            .enqueue(object : Callback<Previsao> {
                override fun onResponse(
                    call: Call<Previsao>,
                    response: Response<Previsao>
                ) {
                    if (response.isSuccessful) {
                        val previsao = response.body()
                        if (previsao != null) {
                            previsaoList.clear()
                            previsaoList.add(previsao)
                            previsaoAdapter.notifyDataSetChanged()
                        } else {
                            Log.e("PrevisaoActivity", "Resposta vazia ou nula")
                        }
                    } else {
                        Log.e("PrevisaoActivity", "Erro na requisição: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Previsao>, t: Throwable) {
                    Log.e("PrevisaoActivity", "Falha na requisição: ${t.message}")
                }
            })
    }
}
