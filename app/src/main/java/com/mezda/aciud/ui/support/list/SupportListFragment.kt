package com.mezda.aciud.ui.support.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.mezda.aciud.R
import com.mezda.aciud.databinding.FragmentSupportBinding
import com.mezda.aciud.ui.BaseFragment
import com.mezda.aciud.ui.support.SupportFlowViewModelProvider
import com.mezda.aciud.ui.support.SupportViewModel
import com.mezda.aciud.utils.SupportAdapter
import com.mezda.aciud.utils.dialogs.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SupportListFragment : BaseFragment() {

    private lateinit var loadingDialog: LoadingDialog
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
        loadingDialog = LoadingDialog(requireActivity())
        if (args.IdLift == 0) {
            toast("Levantamiento Invalido")
            popBack()
        }
        viewModel.messages.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.messageShown()
            }
        })
        viewModel.loading.observe(viewLifecycleOwner, {
            if (it) {
                loadingDialog.apply {
                    show()
                    binding.apply {
                        this.messageText.text = "Enviando"
                    }
                }
            } else {
                loadingDialog.dismiss()
            }
        })

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_support, container, false)
        adapter = SupportAdapter(
            SupportAdapter.SupportListener(
                onClick = {
                    (it.image?.let { it1 ->
                        launchDirection(
                            SupportListFragmentDirections.actionSupportFragmentToSupportImageFragment(
                                it1
                            )
                        )
                    })
                },
                onDelete = {
                    AlertDialog.Builder(requireContext())
                        .setMessage("¿Seguro que deseas borrar este apoyo?")
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                            viewModel.onDelete(it.idSupport)
                        }
                        .setNegativeButton("NO") { dialog, _ ->
                            dialog.dismiss()
                        }.create().show()
                })
        )
        binding.recyclerView.adapter = adapter
        viewModel.supportData.observe(viewLifecycleOwner, {
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