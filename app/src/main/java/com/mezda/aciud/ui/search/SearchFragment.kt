package com.mezda.aciud.ui.search

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mezda.aciud.R
import com.mezda.aciud.data.models.Locality
import com.mezda.aciud.databinding.FragmentSearchBinding
import com.mezda.aciud.ui.BaseFragment
import com.mezda.aciud.ui.MainActivity
import com.mezda.aciud.ui.lifting.LiftingViewModel
import com.mezda.aciud.utils.LiftingAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : BaseFragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var liftingAdapter: LiftingAdapter
    private val searchViewModel by viewModels<SearchViewModel>()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        setHasOptionsMenu(true)
        liftingAdapter = LiftingAdapter(LiftingAdapter.LiftingListener {
            launchDirection(SearchFragmentDirections.actionSearchFragmentToMapsFragment(it))
        })
        binding.liftingRecycler.adapter = liftingAdapter
        binding.liftingRecycler.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.liftingRecycler.setHasFixedSize(true)
        binding.localityTextView.text = Locality.getDefault().nameLocality

        searchViewModel.localities.observe(viewLifecycleOwner, {
            binding.localitySpinner.adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    it.toTypedArray()
            )
        })

        binding.localitySpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        if (p2 != 0) {
                            searchViewModel.searchSuburbs(p2.minus(1))
                        }
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {}
                }

        searchViewModel.suburb.observe(viewLifecycleOwner, {
            binding.suburbSpinner.adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    it.toTypedArray()
            )
        })

        binding.suburbSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        if (p2 != 0) {
                            searchViewModel.searchLifting(p2.minus(1))
                        }
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {}
                }

        searchViewModel.operator.observe(viewLifecycleOwner, {
            (requireActivity() as MainActivity).supportActionBar?.title = it.name
            if (it.name == "Administrador") {
                binding.operatorSpinner.visibility = View.VISIBLE
                searchViewModel.onOperator()
            }
        })

        searchViewModel.operatorList.observe(viewLifecycleOwner, {
            binding.operatorSpinner.adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    it.toTypedArray()
            )
        })

        binding.operatorSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        if (p2 != 0) {
                            searchViewModel.onSearchLifting(p2)
                        }
                    }
                    override fun onNothingSelected(p0: AdapterView<*>?) {}
                }

        searchViewModel.liftingInfo.observe(viewLifecycleOwner, {
            if (it.isEmpty()) {
                binding.liftingRecycler.setBackgroundColor(resources.getColor(android.R.color.darker_gray))
            } else {
                binding.liftingRecycler.setBackgroundColor(resources.getColor(R.color.white))
            }
            liftingAdapter.submit(it)
        })


        searchViewModel.onStart()
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
        }
        return super.onOptionsItemSelected(item)
    }
}