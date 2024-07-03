package com.example.teste

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.io.IOException

class MapaActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val client = OkHttpClient()
    private var markersList: ArrayList<Marker> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        fetchAndShowBusPositions()
        moveCameraToSaoPaulo()
        setupCameraListener()
    }

    private fun moveCameraToSaoPaulo() {
        val saoPaulo = LatLng(-23.5489, -46.6388)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(saoPaulo, 17f))
    }

    private fun fetchAndShowBusPositions() {
        val request = Request.Builder()
            .url("https://aiko-olhovivo-proxy.aikodigital.io/Posicao")
            .addHeader("Authorization", "Bearer 9de3c03ac84f6304d9626c489cb92695ff87fab38a3132ef01ec786650c80a6d") // Adicione seu token de autenticação aqui
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MapaActivity, "Falha ao obter dados", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { responseBody ->
                    val type = object : TypeToken<PosicaoResponse>() {}.type
                    val posicaoResponse: PosicaoResponse = Gson().fromJson(responseBody, type)
                    runOnUiThread {
                        addMarkers(posicaoResponse)
                    }
                }
            }
        })
    }

    private fun addMarkers(posicaoResponse: PosicaoResponse) {
        val customMarkerIcon = bitmapDescriptorFromVector(R.drawable.busmarker)

        for (linha in posicaoResponse.l) {
            for (veiculo in linha.vs) {
                val position = LatLng(veiculo.py, veiculo.px)
                val marker = mMap.addMarker(
                    MarkerOptions()
                        .position(position)
                        .title("Veículo: ${veiculo.p}")
                        .icon(customMarkerIcon)
                )
                if (marker != null) {
                    markersList.add(marker)
                }
            }
        }

        posicaoResponse.l.firstOrNull()?.vs?.firstOrNull()?.let { veiculo ->
            val firstPosition = LatLng(veiculo.py, veiculo.px)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstPosition, 17f))
        }
    }

    private fun setupCameraListener() {

        val MIN_DISTANCE_METERS = 500

        mMap.setOnCameraIdleListener {
            val cameraPosition = mMap.cameraPosition
            val cameraTarget = cameraPosition.target

            for (marker in markersList) {
                val markerPosition = marker.position

                val distance = calculateDistance(cameraTarget, markerPosition)

                marker.isVisible = distance <= MIN_DISTANCE_METERS
            }
        }
    }


    private fun calculateDistance(latLng1: LatLng, latLng2: LatLng): Float {
        val results = FloatArray(1)
        android.location.Location.distanceBetween(latLng1.latitude, latLng1.longitude, latLng2.latitude, latLng2.longitude, results)
        return results[0]
    }

    private fun bitmapDescriptorFromVector(vectorResId: Int): BitmapDescriptor {
        val vectorDrawable: Drawable? = ContextCompat.getDrawable(this, vectorResId)
        vectorDrawable?.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable!!.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}

data class PosicaoResponse(
    val hr: String,
    val l: List<Linha>
)

data class Linha(
    val c: String,
    val cl: Int,
    val sl: Int,
    val lt0: String,
    val lt1: String,
    val qv: Int,
    val vs: List<Veiculo>
)

data class Veiculo(
    val p: Int,
    val a: Boolean,
    val ta: String,
    val py: Double,
    val px: Double
)
