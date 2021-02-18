package com.mezda.aciud.ui.users

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mezda.aciud.R
import com.mezda.aciud.databinding.FragmentUsersBinding
import com.mezda.aciud.ui.login.LoginViewModel
import com.mezda.aciud.utils.OperatorsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UsersFragment : Fragment() {

    private lateinit var binding: FragmentUsersBinding
    private val viewModel by viewModels<UserViewModel>()
    lateinit var adapter: OperatorsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_users, container, false)
        adapter = OperatorsAdapter(OperatorsAdapter.OperatorListener(onClick = { operator ->
            viewModel.updateOperator(operators = operator)
        }))
        binding.userRecycler.layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL, false)
        binding.userRecycler.adapter = adapter
        binding.userRecycler.setHasFixedSize(true)

        viewModel.messages.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.messageShown()
            }
        })

        viewModel.users.observe(viewLifecycleOwner, {
            binding.emptyView.visibility = if (it.isNotEmpty()) View.GONE else View.VISIBLE
            adapter.submitList(it.toMutableList())
        })

        viewModel.getOperators()
        return binding.root
    }
}