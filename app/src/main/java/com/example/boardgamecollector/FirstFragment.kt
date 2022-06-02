package com.example.boardgamecollector

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.example.boardgamecollector.databinding.FragmentFirstBinding
import java.io.File

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        //SPRAWDZANIE CZY ISTNIEJE PLIK XML
        val filename = "accountInfo.xml"
        val path = requireContext().filesDir
        val inDir = File(path, "XML")
        if(inDir.exists()) {
            val file = File(inDir, filename)
            if (file.exists() && File(path, "userdate.csv").exists()) {
                findNavController().navigate(R.id.action_FirstFragment_to_summaryFragment)
            }
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //PRZEKZYWANIE NAZWY UŻYTKOWNIKA DO SF
        binding.toSFButtonFF.setOnClickListener {
            var userName: String
            if(binding.nicknameInputFF.text.isEmpty())
                Toast.makeText(context, "Please, enter a username.", Toast.LENGTH_SHORT).show()
            else {
                userName = binding.nicknameInputFF.text.toString()
                setFragmentResult("FFtoSF", bundleOf("userName" to userName))
                findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}