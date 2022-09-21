package com.example.boardgamecollector

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.example.boardgamecollector.databinding.FragmentSummaryBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

class SummaryFragment : Fragment() {

    private var _binding: FragmentSummaryBinding? = null
    private val binding get() = _binding!!

    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
    var lastSyncDate = sdf.format(Date())
    var userName: String = ""
    var flagGame: Boolean = true
    var flagExpansion: Boolean = true

    fun loadDataFromCSV() {
        //ŁADOWANIE DANYCH Z PLIKU CSV
        val filename = "userdate.csv"
        val inputStream = requireContext().openFileInput(filename)
        val inputString = inputStream.bufferedReader().use {it.readText()}
        var userdate = UserDate(inputString)
        binding.syncText2SUM.text = userdate.getLSD()
        userName = userdate.getUN()
        binding.usernameTextSUM.text = userdate.getUN()
        lastSyncDate = userdate.getLSD()

        binding.gameNumText2SUM.text = userdate.getGamesNumber()
        if(userdate.getGamesNumber()[0] == '0')
            flagGame = false

        binding.DLCNumText2SUM.text = userdate.getExpansionsNumber()
        if(userdate.getExpansionsNumber()[0] == '0')
            flagExpansion = false

        inputStream.close()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSummaryBinding.inflate(inflater, container, false)
        loadDataFromCSV()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.syncButtonSUM.setOnClickListener {
            setFragmentResult("SUMtoSF", bundleOf(Pair("userName", userName),
                Pair("lastSync", lastSyncDate)))
            findNavController().navigate(R.id.action_summaryFragment_to_SecondFragment)
        }

        binding.gameNumButtonSUM.setOnClickListener {
            if(flagGame)
                findNavController().navigate(R.id.action_summaryFragment_to_gameListFragment)
            else
                Toast.makeText(requireContext(), "No games to show", Toast.LENGTH_SHORT).show()
        }

        binding.DLCNumButtonSUM.setOnClickListener {
            if(flagExpansion)
                findNavController().navigate(R.id.action_summaryFragment_to_dlcListFragment)
            else
                Toast.makeText(requireContext(), "No expansions to show", Toast.LENGTH_SHORT).show()
        }

        binding.exitWoClearSUM.setOnClickListener {
            exitProcess(0)
        }

        binding.exitWClearSUM.setOnClickListener{
            binding.exitLayoutSUM.isVisible = false
            binding.commitClearAllLayoutSUM.isVisible = true
        }

        binding.noCommitButtonSUM.setOnClickListener{
            binding.exitLayoutSUM.isVisible = true
            binding.commitClearAllLayoutSUM.isVisible = false
        }

        binding.yesCommitButtonSUM.setOnClickListener {
            var path = requireContext().filesDir
            var inDir = File(path, "XML")
            inDir.deleteRecursively()

            var filename = "userdate.csv"
            var file = File(path, filename)
            file.delete()

            filename = "gameID.csv"
            file = File(path, filename)
            file.delete()

            path = requireContext().filesDir.parentFile
            path = File(path, "databases")
            path.deleteRecursively()
            exitProcess(0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


