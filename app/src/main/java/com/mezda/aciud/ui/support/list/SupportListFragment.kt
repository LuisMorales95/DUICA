package com.mezda.aciud.ui.support.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.mezda.aciud.R
import com.mezda.aciud.databinding.FragmentSupportBinding
import com.mezda.aciud.ui.BaseFragment
import com.mezda.aciud.ui.support.SupportFlowViewModelProvider
import com.mezda.aciud.ui.support.SupportViewModel
import com.mezda.aciud.utils.SupportAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SupportListFragment : BaseFragment() {

    private lateinit var adapter: SupportAdapter
    private lateinit var binding: FragmentSupportBinding
    private val args: SupportListFragmentArgs by navArgs()

    @Inject
    lateinit var factory: SupportFlowViewModelProvider
    private val viewModel by navGraphViewModels<SupportViewModel>(R.id.nav_support) {
        factory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (args.IdLift == 0) {
            toast("Levantamiento Invalido")
            popBack()
        }
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_support, container,false)
        adapter = SupportAdapter()
        binding.recyclerView.adapter = adapter
        viewModel.supportData.observe(viewLifecycleOwner ,{
            if (it.isEmpty()) {
                toast("Sin Datos")
            }
            adapter.submit(it)
        })
        binding.supportAddFab.setOnClickListener {
            launchDirection(SupportListFragmentDirections.actionSupportFragmentToSupportNewFragment())
        }
        viewModel.getSupports(args.IdLift)
        return binding.root
    }
}