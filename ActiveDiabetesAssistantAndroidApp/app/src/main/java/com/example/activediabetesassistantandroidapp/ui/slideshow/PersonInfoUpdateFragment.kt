package com.example.activediabetesassistantandroidapp.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.activediabetesassistantandroidapp.databinding.FragmentPersonInfoUpdateBinding

class PersonInfoUpdateFragment : Fragment() {

    private var _binding: FragmentPersonInfoUpdateBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val personInfoUpdateViewModel =
            ViewModelProvider(this).get(PersonInfoUpdateViewModel::class.java)

        _binding = FragmentPersonInfoUpdateBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSlideshow
        personInfoUpdateViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}