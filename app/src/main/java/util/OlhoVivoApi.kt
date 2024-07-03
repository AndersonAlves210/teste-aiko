package util

import data.Linhas
import data.Previsao
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OlhoVivoApi {


    @GET("Linha/Buscar")
    fun getLinhas(@Query("termosBusca") termosBusca: String): Call<List<Linhas>>


    @GET("Previsao/Linha")
    fun getPrevisaoPorLinha(
        @Query("codigoLinha") codigoLinha: String
    ): Call<Previsao>

}