package pl.bk20.forest.catalogue.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.bk20.forest.catalogue.domain.model.Plant
import pl.bk20.forest.databinding.ItemPlantCardBinding

class PlantAdapter(
    private val onClick: (Plant) -> Unit,
) : ListAdapter<Plant, PlantAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(
        private val binding: ItemPlantCardBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(plant: Plant) {
            // drawableResId is precomputed at parse time, so no
            // Resources.getIdentifier work runs on the bind hot path.
            binding.imagePlant.setImageResource(plant.drawableResId)
            binding.imagePlant.contentDescription = plant.commonName ?: plant.scientificName
            if (plant.commonName != null) {
                binding.textCommonName.text = plant.commonName
                binding.textScientificName.text = plant.scientificName
                binding.textScientificName.visibility = android.view.View.VISIBLE
            } else {
                binding.textCommonName.text = plant.scientificName
                binding.textScientificName.visibility = android.view.View.GONE
            }
            binding.textFamily.text = plant.family
            binding.root.setOnClickListener { onClick(plant) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemPlantCardBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Plant>() {
            override fun areItemsTheSame(old: Plant, new: Plant) = old.id == new.id
            override fun areContentsTheSame(old: Plant, new: Plant) = old == new
        }
    }
}
