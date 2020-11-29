package com.mezda.aciud.ui.lifting

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import com.mezda.aciud.R
import com.mezda.aciud.databinding.FragmentLiftingBinding

class LiftingFragment : Fragment() {

    private lateinit var binding: FragmentLiftingBinding
    private lateinit var viewModel: LiftingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_lifting, container, false)
        return binding.root
    }

}