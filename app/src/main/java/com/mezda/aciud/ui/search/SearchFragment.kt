package com.mezda.aciud.ui.search

import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mezda.aciud.R
import com.mezda.aciud.data.models.LiftingInfo
import com.mezda.aciud.data.models.Locality
import com.mezda.aciud.data.models.Locations
import com.mezda.aciud.databinding.FragmentSearchBinding
import com.mezda.aciud.ui.BaseFragment
import com.mezda.aciud.ui.MainActivity
import com.mezda.aciud.utils.LiftingAdapter
import com.mezda.aciud.utils.SuburbAutoCompleteValidator
import com.mezda.aciud.utils.SuburbFocusListener
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
class SearchFragment : BaseFragment(), View.OnClickListener {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var liftingAdapter: LiftingAdapter
    private val searchViewModel by viewModels<SearchViewModel>()
    private var isAdmin = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        setHasOptionsMenu(true)
        liftingAdapter = LiftingAdapter(
            LiftingAdapter.LiftingListener(editListener = { liftingInfo: LiftingInfo ->
                launchDirection(SearchFragmentDirections.actionSearchFragmentToUserInfoFragment(liftingInfo))
            }, mapListener = { info: LiftingInfo ->
                val locations = Locations(mutableListOf(info), singleLocation = true)
                launchDirection(
                    SearchFragmentDirections.actionSearchFragmentToMapsFragment(
                        locations
                    )
                )
            })
        )
        binding.liftingRecycler.adapter = liftingAdapter
        binding.liftingRecycler.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.liftingRecycler.setHasFixedSize(true)
        binding.localityTextView.text = Locality.getDefault().nameLocality
        //binding.localityTextView.visibility = View.GONE

        searchViewModel.operator.observe(viewLifecycleOwner, {
            (requireActivity() as MainActivity).supportActionBar?.title = it.name
            if (it.name == "Administrador") {
                isAdmin = true
                binding.operatorSpinner.visibility = View.VISIBLE
                searchViewModel.onOperator()
                binding.searchButton.visibility = View.VISIBLE
            }
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


        searchViewModel.onStart()
        binding.searchButton.setOnClickListener(this)
        binding.mapList.setOnClickListener(this)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.lifting_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> {
                findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToLiftingFragment())
            }
            R.id.action_flow -> {
                findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToUserInfoFragment())
            }
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
        }
    }
}