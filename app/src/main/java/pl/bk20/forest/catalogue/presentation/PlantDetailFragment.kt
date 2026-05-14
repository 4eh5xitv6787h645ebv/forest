package pl.bk20.forest.catalogue.presentation

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import pl.bk20.forest.R
import pl.bk20.forest.catalogue.domain.model.Plant
import pl.bk20.forest.databinding.FragmentPlantDetailBinding

class PlantDetailFragment : Fragment() {

    private val viewModel: PlantDetailViewModel by viewModels { PlantDetailViewModel.Factory }

    private var _binding: FragmentPlantDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlantDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val plantId = arguments?.getInt(ARG_PLANT_ID, -1) ?: -1
        if (plantId <= 0) {
            parentFragmentManager.popBackStack()
            return
        }
        viewModel.load(plantId)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.plant.collect { it?.let(::render) }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun render(plant: Plant) {
        val b = _binding ?: return
        b.imagePlant.setImageResource(plant.drawableResId)
        b.imagePlant.contentDescription = plant.commonName ?: plant.scientificName
        if (plant.commonName != null) {
            b.textCommonName.text = plant.commonName
            b.textScientificName.text = plant.scientificName
            b.textScientificName.visibility = View.VISIBLE
        } else {
            b.textCommonName.text = plant.scientificName
            b.textScientificName.visibility = View.GONE
        }
        b.textFamily.text = getString(R.string.detail_family, plant.family)
        b.textHabit.text = getString(R.string.detail_habit, plant.habit.code.replace('_', ' '))
        b.textHeight.text = getString(
            R.string.detail_height,
            plant.heightMinMeters, plant.heightMaxMeters,
        )
        b.textRegions.text = getString(
            R.string.detail_regions,
            plant.regions.joinToString(", "),
        )
        b.buttonWikipedia.setOnClickListener { openWikipedia(plant.wikipediaUrl) }
    }

    private fun openWikipedia(url: String) {
        val uri = runCatching { Uri.parse(url) }.getOrNull() ?: return
        // Defensive: only allow http(s) URLs to wikipedia hosts. The URL
        // comes from the bundled JSON today, but this prevents a stray entry
        // (or a future contribution) from triggering an arbitrary intent.
        val safe = uri.scheme in WEB_SCHEMES && uri.host?.endsWith("wikipedia.org") == true
        if (!safe) {
            Log.w(TAG, "Refusing to open non-wikipedia URL: $url")
            return
        }
        try {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), R.string.detail_no_browser, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val ARG_PLANT_ID = "plantId"
        private const val TAG = "PlantDetailFragment"
        private val WEB_SCHEMES = setOf("https", "http")
    }
}
