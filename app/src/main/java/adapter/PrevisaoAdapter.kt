package adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.teste.R
import data.Previsao

class PrevisaoAdapter(private val previsoes: List<Previsao>) :
    RecyclerView.Adapter<PrevisaoAdapter.PrevisaoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrevisaoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.previsao_adapter, parent, false)
        return PrevisaoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PrevisaoViewHolder, position: Int) {
        val previsao = previsoes[position]
        holder.horarioTextView.text = "Horário: ${previsao.hr ?: "N/A"}"

        if (previsao.ps != null && previsao.ps.isNotEmpty()) {
            val parada = previsao.ps[0]
            holder.codigoParadaTextView.text = "Código da Parada: ${parada.cp?.toString() ?: "N/A"}"
            holder.nomeParadaTextView.text = "Nome da Parada: ${parada.np ?: "N/A"}"

            if (parada.vs != null && parada.vs.isNotEmpty()) {
                val veiculo = parada.vs[0]
                holder.prefixoVeiculoTextView.text = "Prefixo do Veículo: ${veiculo.p ?: "N/A"}"
                holder.tempoPrevistoTextView.text = "Tempo Previsto: ${veiculo.t ?: "N/A"}"
                holder.tempoAtualizacaoTextView.text = "Tempo de Atualização: ${veiculo.ta ?: "N/A"}"
            } else {
                holder.prefixoVeiculoTextView.text = "Prefixo do Veículo: N/A"
                holder.tempoPrevistoTextView.text = "Tempo Previsto: N/A"
                holder.tempoAtualizacaoTextView.text = "Tempo de Atualização: N/A"
            }
        } else {
            holder.codigoParadaTextView.text = "Código da Parada: N/A"
            holder.nomeParadaTextView.text = "Nome da Parada: N/A"
            holder.prefixoVeiculoTextView.text = "Prefixo do Veículo: N/A"
            holder.tempoPrevistoTextView.text = "Tempo Previsto: N/A"
            holder.tempoAtualizacaoTextView.text = "Tempo de Atualização: N/A"
        }
    }

    override fun getItemCount(): Int {
        return previsoes.size
    }

    class PrevisaoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val horarioTextView: TextView = itemView.findViewById(R.id.horarioTextView)
        val codigoParadaTextView: TextView = itemView.findViewById(R.id.codigoParadaTextView)
        val nomeParadaTextView: TextView = itemView.findViewById(R.id.nomeParadaTextView)
        val prefixoVeiculoTextView: TextView = itemView.findViewById(R.id.prefixoVeiculoTextView)
        val tempoPrevistoTextView: TextView = itemView.findViewById(R.id.tempoPrevistoTextView)
        val tempoAtualizacaoTextView: TextView = itemView.findViewById(R.id.tempoAtualizacaoTextView)
    }
}
