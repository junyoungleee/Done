package com.palette.done.view.main.done

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxItemDecoration
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.palette.done.DoneApplication
import com.palette.done.R
import com.palette.done.data.db.entity.Routine
import com.palette.done.data.remote.model.dones.Tags
import com.palette.done.data.remote.repository.DoneServerRepository
import com.palette.done.databinding.FragmentDoneBinding
import com.palette.done.databinding.FragmentDoneHashtagBinding
import com.palette.done.databinding.FragmentObNicknameBinding
import com.palette.done.view.adapter.HashTagAdapter
import com.palette.done.view.adapter.RoutineTagAdapter
import com.palette.done.view.util.NetworkManager
import com.palette.done.viewmodel.*
import okhttp3.internal.notify

class DoneHashtagFragment : Fragment() {

    private var _binding: FragmentDoneHashtagBinding? = null
    private val binding get() = _binding!!

    private val hashTagVM: HashTagViewModel by activityViewModels() {
        HashTagViewModelFactory(DoneServerRepository(), DoneApplication().doneRepository)
    }
    private val doneVM: DoneEditViewModel by activityViewModels() {
        DoneEditViewModelFactory(DoneServerRepository(), DoneApplication().doneRepository)
    }

    private val tagAdapter = HashTagAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentDoneHashtagBinding.inflate(inflater, container, false)

        checkNetworkState()

        hashTagVM.get6Tags()
        setRandomButton()

        initHashTagRecyclerView()

        return binding.root
    }

    private fun initHashTagRecyclerView() {
        val tagLayoutManager = FlexboxLayoutManager(context)
        tagLayoutManager.flexDirection = FlexDirection.ROW
        tagLayoutManager.justifyContent = JustifyContent.CENTER

        val tagDecoration = FlexboxItemDecoration(context)
        tagDecoration.setDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.tag_divider))

        with(binding.rcHashTag) {
            adapter = tagAdapter
            layoutManager = tagLayoutManager
            overScrollMode = View.OVER_SCROLL_NEVER
            addItemDecoration(tagDecoration)
        }
        hashTagVM.tags.observe(viewLifecycleOwner) { tagList ->
            tagList.let { tagAdapter.submitList(it) }
        }
        tagAdapter.setTagItemClickListener(object : HashTagAdapter.OnTagItemClickListener {
            override fun onTagClick(v: View, tag: Tags) {
                // 해시 태그 클릭 리스너
                doneVM._selectedHashtag.value = tag
                tagAdapter.notifyDataSetChanged()
            }
        })
        doneVM.selectedHashtag.observe(viewLifecycleOwner) {
            if (it.tag_no == 0) {
                tagAdapter.initClickedPosition()
                tagAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun checkNetworkState() {
        if (!NetworkManager.checkNetworkState(requireActivity())) {
            NetworkManager.showRequireNetworkToast(requireActivity())
        }
    }

    private fun setRandomButton() {
        binding.llHashTagRandom.setOnClickListener {
            if (NetworkManager.checkNetworkState(requireActivity())) {
                tagAdapter.initClickedPosition()
                tagAdapter.notifyDataSetChanged()
                hashTagVM.get6Tags()
            } else {
                NetworkManager.showRequireNetworkToast(requireActivity())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}