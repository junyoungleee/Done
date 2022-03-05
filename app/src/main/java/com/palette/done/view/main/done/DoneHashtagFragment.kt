package com.palette.done.view.main.done

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.palette.done.databinding.FragmentDoneBinding
import com.palette.done.databinding.FragmentDoneHashtagBinding
import com.palette.done.databinding.FragmentObNicknameBinding

class DoneHashtagFragment : Fragment() {

    private var _binding: FragmentDoneHashtagBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentDoneHashtagBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}