package com.mezda.aciud.ui.search

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.mezda.aciud.R
import com.mezda.aciud.data.models.LiftingInfo
import com.mezda.aciud.data.models.Locality
import com.mezda.aciud.data.models.Locations
import com.mezda.aciud.databinding.FragmentSearchBinding
import com.mezda.aciud.databinding.OptionsMenuBinding
import com.mezda.aciud.ui.BaseFragment
import com.mezda.aciud.ui.MainActivity
import com.mezda.aciud.utils.LiftingAdapter
import com.mezda.aciud.utils.enums.EnumOptions
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@Suppress("DEPRECATION")
@AndroidEntryPoint
class SearchFragment : BaseFragment(), View.OnClickListener {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var liftingAdapter: LiftingAdapter
    private val searchViewModel by viewModels<SearchViewModel>()
    private var isAdmin = false
    private var accountsMenuItem: MenuItem? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.lifting_menu, menu)
        accountsMenuItem = menu.findItem(R.id.action_accounts)
        accountsMenuItem?.isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
        initComponent()
    }

    private fun initComponent() {
        liftingAdapter = LiftingAdapter(
            LiftingAdapter.LiftingListener( selectedItem = {
                optionsDialog { option ->
                    when (option) {
                        EnumOptions.SUPPORT -> {
                            it.idLifting?.let { id ->
                                if (id != 0) {
                                    launchDirection(
                                        SearchFragmentDirections.actionSearchFragmentToSupportFragment(
                                            id
                                        )
                                    )
                                    return@let
                                }
                                toast("Identificador de Levantamiento Invalido")
                            }
                        }
                        EnumOptions.EDIT -> {
                            launchDirection(
                                SearchFragmentDirections.actionSearchFragmentToUserInfoFragment(
                                    it
                                )
                            )
                        }
                        EnumOptions.MAP -> {
                            val locations = Locations(mutableListOf(it), singleLocation = true)
                            launchDirection(
                                SearchFragmentDirections.actionSearchFragmentToMapsFragment(
                                    locations
                                )
                            )
                        }
                    }
                }.show()
            })
        )
        binding.liftingRecycler.adapter = liftingAdapter
        binding.liftingRecycler.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.liftingRecycler.setHasFixedSize(true)
        binding.localityTextView.text = Locality.getDefault().nameLocality

        searchViewModel.operator.observe(viewLifecycleOwner, {
            Timber.e(Gson().toJson(it))
            (requireActivity() as MainActivity).supportActionBar?.title = it.name
            if (it.isAdmin == true) {
                isAdmin = true
                accountsMenuItem?.isVisible = true
                binding.operatorSpinner.visibility = View.VISIBLE
                searchViewModel.onOperator()
                binding.searchButton.visibility = View.VISIBLE
            }
            liftingAdapter.setPermissionModify(it.allowModification ?: false)
            binding.addFab.visibility = if (it.allowCapture == true) View.VISIBLE else View.GONE
        })

        searchViewModel.operatorList.observe(viewLifecycleOwner, {
            binding.operatorSpinner.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                it.toTypedArray()
            )
        })

        searchViewModel.liftingInfo.observe(viewLifecycleOwner, {
            if (it.isEmpty()) {
                binding.mapList.visibility = View.GONE
                binding.liftingRecycler.setBackgroundColor(resources.getColor(R.color.light_gray))
            } else {
                binding.mapList.visibility = View.VISIBLE
                binding.liftingRecycler.setBackgroundColor(resources.getColor(R.color.white))
            }
            liftingAdapter.submit(it)
        })
        searchViewModel.messages.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                searchViewModel.messageShown()
            }
        })

        searchViewModel.onStart()
        binding.searchButton.setOnClickListener(this)
        binding.mapList.setOnClickListener(this)
        binding.addFab.setOnClickListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_accounts -> {
                launchDirection(SearchFragmentDirections.actionSearchFragmentToUsersFragment())
            }
            //R.id.action_add -> {
            //    findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToLiftingFragment())
            //}
            //R.id.action_flow -> {
            //    findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToUserInfoFragment())
            //}
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            binding.searchButton.id -> {
                if (isAdmin) {
                    if (binding.operatorSpinner.selectedItemPosition != 0) {
                        searchViewModel.onSearch(
                            binding.operatorSpinner.selectedItemPosition,
                            isAdmin
                        )
                    } else {
                        Toast.makeText(requireContext(), "Datos faltante", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    searchViewModel.onSearch()
                }
            }
            binding.mapList.id -> {
                val locations =
                    Locations(list = liftingAdapter.getCurrentItems(), singleLocation = false)
                launchDirection(
                    SearchFragmentDirections.actionSearchFragmentToMapsFragment(locations)
                )
            }
            binding.addFab.id -> {
                findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToUserInfoFragment())
            }
        }
    }

    private fun optionsDialog(onSelect: (EnumOptions) -> Unit): Dialog {
        val dialog = Dialog(requireContext())
        val binding = OptionsMenuBinding.inflate(dialog.layoutInflater)
        binding.supportButton.setOnClickListener {
            onSelect(EnumOptions.SUPPORT)
            dialog.dismiss()
        }
        binding.editButton.setOnClickListener {
            onSelect(EnumOptions.EDIT)
            dialog.dismiss()
        }
        binding.mapButton.setOnClickListener {
            onSelect(EnumOptions.MAP)
            dialog.dismiss()
        }
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setContentView(binding.root)
        return dialog
    }
}