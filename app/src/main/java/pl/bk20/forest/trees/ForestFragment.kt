package pl.bk20.forest.trees

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import pl.bk20.forest.ForestApplication
import pl.bk20.forest.R
import pl.bk20.forest.catalogue.util.PlantSlots
import pl.bk20.forest.databinding.FragmentForestBinding
import kotlin.random.Random

class ForestFragment : Fragment() {

    private val viewModel: ForestViewModel by viewModels { ForestViewModel.Factory }

    private var _binding: FragmentForestBinding? = null
    private val binding get() = _binding!!

    private var renderedTreeCount: Int = -1
    private lateinit var plantSlots: PlantSlots

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentForestBinding.inflate(inflater, container, false)
        plantSlots = (requireActivity().application as ForestApplication).plantSlots
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.trees.collect { updateUserInterface(it) }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                plantSlots.readiness.collect { ready ->
                    // The first render may have used placeholder drawables
                    // because the app-startup pre-warm hadn't finished. When
                    // the slot table fills, redraw with the real plants.
                    if (ready && renderedTreeCount > 0) {
                        generateTrees(renderedTreeCount)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding?.constraintLayoutTrees?.removeAllViews()
        _binding = null
        renderedTreeCount = -1
        super.onDestroyView()
    }

    private fun updateUserInterface(forestState: ForestState) {
        val treeCount = forestState.treeCount
        binding.apply {
            textTreesCollected.text = treeCount.toString()
            textTreesCollectedLabel.text = resources.getQuantityString(R.plurals.trees, treeCount)
        }
        // Only re-layout when the count actually changed. Without this, every
        // step-count emission (potentially many per second) tore down and
        // re-inflated up to N vector drawables — a real ANR / jank source.
        if (treeCount != renderedTreeCount) {
            generateTrees(treeCount)
            renderedTreeCount = treeCount
        }
    }

    private fun generateTrees(treeCount: Int) {
        val parentLayout = binding.constraintLayoutTrees
        parentLayout.removeAllViews()
        if (treeCount <= 0) return
        val gapCount = treeCount + 1
        // Deterministic jitter seeded by treeCount so positions stay stable
        // across re-emissions when the count happens to match.
        val jitter = Random(treeCount.toLong() * 31)
        repeat(treeCount) { slot ->
            val fixedPosition = (slot + 1.0) / gapCount
            val randomOffset = (jitter.nextDouble() - 0.5) / 5
            createTree(parentLayout, fixedPosition + randomOffset, slot)
        }
    }

    private fun createTree(parentLayout: ConstraintLayout, horizontalPosition: Double, slot: Int) {
        val plantImageView = ImageView(context)
        plantImageView.setImageResource(
            plantSlots.drawableForSlot(slot, R.drawable.tree_collected)
        )
        parentLayout.addView(plantImageView)
        plantImageView.updateLayoutParams<ConstraintLayout.LayoutParams> {
            startToStart = parentLayout.id
            endToEnd = parentLayout.id
            bottomToBottom = parentLayout.id
            horizontalBias = horizontalPosition.toFloat()
        }
    }
}
