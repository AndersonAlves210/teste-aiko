package data

data class Previsao(
    val hr: String?,
    val ps: List<Paradas>?
)

data class Paradas(
    val cp: Int?,
    val np: String?,
    val py: Double?,
    val px: Double?,
    val vs: List<Veiculos>?
)

data class Veiculos(
    val p: String?,
    val t: String?,
    val a: Boolean?,
    val ta: String?,
    val py: Double?,
    val px: Double?
)

