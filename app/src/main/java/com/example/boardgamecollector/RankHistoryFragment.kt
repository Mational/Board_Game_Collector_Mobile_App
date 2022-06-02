package com.example.boardgamecollector

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.boardgamecollector.databinding.FragmentRankHistoryBinding

class RankHistoryFragment : Fragment() {

    private var _binding: FragmentRankHistoryBinding? = null
    private val binding get() = _binding!!

    var gameID: Int = 0
    lateinit var ranks: MutableList<Rank>
    private lateinit var tableRanksGR: TableLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRankHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        readID()
        ranks = loadDataGR()
        tableRanksGR = requireView().findViewById(R.id.rankTableGR)
        showDataGR(ranks)

        binding.toGLfromGR.setOnClickListener {
            findNavController().navigate(R.id.action_rankHistoryFragment_to_gameListFragment)
        }
    }

    private fun readID() {
        //ŁADOWANIE DANYCH Z PLIKU CSV
        val filename = "gameID.csv"
        val inputStream = requireContext().openFileInput(filename)
        val inputString = inputStream.bufferedReader().use {it.readText()}
        gameID = inputString.toInt()
        inputStream.close()
    }

    private fun loadDataGR(): MutableList<Rank> {
        val dbHandler = MyDBHandler(this.requireContext(), null, null, 1)
        var result = dbHandler.findRankOfGame(gameID)
        return result
    }

    private fun showDataGR(ranks: MutableList<Rank>) {
        val leftRowMargin = 0
        val topRowMargin = 0
        val rightRowMargin = 0
        val bottomRowMargin = 0

        val rows = ranks.size
        var textSpacer: TextView?

        // -1 oznacza nagłówek
        for (i in -1 until rows) {
            lateinit var row: Rank

            if (i < 0) {
                //nagłówek
                textSpacer = TextView(context)
                textSpacer.text = ""
            } else
                row = ranks[i]

            //TextView do id
            val tv = TextView(context)
            tv.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT
            )
            tv.gravity = Gravity.CENTER
            tv.setPadding(10, 10, 10, 10)
            tv.setTextColor(Color.parseColor("#000000"))

            if (i == -1) run {
                tv.text = "ID"
                tv.setBackgroundColor(Color.parseColor("#518EF5"))
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, 28.0f)
            } else run {
                tv.setBackgroundColor(Color.parseColor("#46ACF2"))
                val j = i+1
                tv.text = j.toString()
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, 25.0f)
                tv.setTypeface(tv.typeface, Typeface.BOLD_ITALIC)
            }

            //TextView do rank
            val tv2 = TextView(context)
            tv2.foregroundGravity = Gravity.CENTER
            tv2.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT
            )

            tv2.setPadding(10, 10, 10, 10)
            tv2.setTextColor(Color.parseColor("#000000"))

            if (i == -1) {
                tv2.setBackgroundColor(Color.parseColor("#46ACF2"))
                tv2.text = "Rank"
            } else {
                tv2.setBackgroundColor(Color.parseColor("#C3DFEB"))
                tv2.text = row?.rank.toString()
            }

            //TextView do daty
            val tv3 = TextView(context)
            tv3.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT
            )

            tv3.gravity = Gravity.CENTER
            tv3.setTextColor(Color.parseColor("#000000"))
            tv3.setPadding(5, 10, 5, 10)

            if (i == -1) {
                tv3.text = "Date"
                tv3.setBackgroundColor(Color.parseColor("#46ACF2"))
            } else {
                tv3.setBackgroundColor(Color.parseColor("#C3DFEB"))
                tv3.text = row?.date
            }

            // add table row
            val tr = TableRow(context)
            tr.id = i + 1
            val trParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT
            )
            trParams.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin)
            tr.gravity = Gravity.CENTER
            tr.layoutParams = trParams

            tr.addView(tv)
            tr.addView(tv2)
            tr.addView(tv3)

            if(i < 0)   tr.setPadding(5, 5, 5, 0)
            else        tr.setPadding(5, 0, 5, 5)

            tableRanksGR.setBackgroundColor(Color.parseColor("#000000"))
            tableRanksGR.addView(tr, trParams)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}