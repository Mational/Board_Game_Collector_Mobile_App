package com.example.boardgamecollector

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.os.AsyncTask
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.boardgamecollector.databinding.FragmentGameListBinding
import java.net.URL

class GameListFragment : Fragment() {

    private var _binding: FragmentGameListBinding? = null
    private val binding get() = _binding!!
    private lateinit var tableGamesGL: TableLayout

    var flagSortIDGL: Boolean = true
    var flagSortNameGL: Boolean = true
    var flagSortRankGL: Boolean = true
    var flagSortYearGL: Boolean = true

    private inner class DownloadImageFromInternet(var imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {
        override fun onPreExecute() {}

        override fun doInBackground(vararg urls: String): Bitmap? {
            val imageURL = urls[0]
            var image: Bitmap? = null
            try {
                val `in` = URL(imageURL).openStream()
                image = BitmapFactory.decodeStream(`in`)
                image.width = 10
                image.height = 10
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
            return image
        }
        override fun onPostExecute(result: Bitmap?) {
            imageView.setImageBitmap(result)
        }
    }

    private fun loadDataGL(): MutableList<Game> {
        val dbHandler = MyDBHandler(this.requireContext(), null, null, 1)
        var result = dbHandler.findGames("boardgame")
        return result
    }

    private fun sortDataGL(sCol: String, direct: String): MutableList<Game> {
        val dbHandler = MyDBHandler(this.requireContext(), null, null, 1)
        var result = dbHandler.sortGames("boardgame", direct, sCol)
        return result
    }

    private fun showDataGL(games: MutableList<Game>) {
        val leftRowMargin = 0
        val topRowMargin = 0
        val rightRowMargin = 0
        val bottomRowMargin = 0

        val rows = games.size
        var textSpacer: TextView?

        // -1 oznacza nagłówek
        for (i in -1 until rows) {
            lateinit var row: Game

            if (i < 0) {
                //nagłówek
                textSpacer = TextView(context)
                textSpacer.text = ""
            } else
                row = games[i]

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

            //TextView do ikony
            val tv2 = ImageView(context)
            tv2.foregroundGravity = Gravity.CENTER

            tv2.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.MATCH_PARENT
            )

            tv2.setPadding(10, 10, 10, 10)

            if (i == -1)
                tv2.setBackgroundColor(Color.parseColor("#46ACF2"))
            else {
                tv2.setBackgroundColor(Color.parseColor("#C3DFEB"))
                DownloadImageFromInternet(tv2).execute(row.gameThumbnail.toString())
            }

            //TextView do liczby graczy
            val tv3 = TextView(context)
            tv3.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.MATCH_PARENT
            )

            tv3.gravity = Gravity.CENTER
            tv3.setTextColor(Color.parseColor("#000000"))

            tv3.setPadding(5, 10, 5, 10)
            if (i == -1) {
                tv3.text = "Players"
                tv3.setBackgroundColor(Color.parseColor("#46ACF2"))
                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, 28.0f)
            } else {
                tv3.setBackgroundColor(Color.parseColor("#C3DFEB"))
                tv3.text = "${row?.gameMinPlayers} - ${row?.gameMaxPlayers}"
                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, 23.0f)
            }

            //LinearLayout do wyświetlania informacji o grze
            val layCustomer = LinearLayout(context)
            layCustomer.orientation = LinearLayout.VERTICAL
            layCustomer.setBackgroundColor(Color.parseColor("#C3DFEB"))

            val tv4a = TextView(context)
            tv4a.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT
            )

            tv4a.gravity = Gravity.CENTER
            tv4a.setTextColor(Color.parseColor("#000000"))

            tv4a.maxWidth = 240

            if (i == -1) {
                tv4a.text = "Informations"
                tv4a.setBackgroundColor(Color.parseColor("#46ACF2"))
                tv4a.setPadding(10, 10, 10, 10)
                tv4a.setTextSize(TypedValue.COMPLEX_UNIT_PX, 28.0f)
            } else {
                tv4a.setBackgroundColor(Color.parseColor("#C3DFEB"))
                tv4a.text = "${row?.originalTitle}\n(${row?.releaseYear})"
                tv4a.setTextColor(Color.parseColor("#0000ff"))
                tv4a.setPadding(5, 5, 5, 5)
                tv4a.setTextSize(TypedValue.COMPLEX_UNIT_PX, 20.0f)
                tv4a.setOnClickListener {
                    val outputData = row?.bggId
                    val filename = "gameID.csv"
                    val file = requireContext().openFileOutput(filename, Context.MODE_PRIVATE)
                    file.write(outputData.toString().toByteArray())
                    file.flush()
                    file.close()
                    findNavController().navigate(R.id.action_gameListFragment_to_rankHistoryFragment)
                }
            }
            layCustomer.addView(tv4a)

            if(i > -1) {
                var sv4b1 = NestedScrollView(requireContext())
                sv4b1.layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    300
                )
                sv4b1.setPadding(0, 0, 0, 20)

                var tv4b2 = TextView(context)
                tv4b2.layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT,
                )

                tv4b2.gravity = Gravity.CENTER
                tv4b2.setTextSize(TypedValue.COMPLEX_UNIT_PX, 20.0f)
                tv4b2.setPadding(10, 10, 10, 10)
                tv4b2.setTextColor(Color.parseColor("#000000"))
                var itemDescription2 = row.gameDescription
                itemDescription2 = itemDescription2?.replace("&#10;", " ")
                itemDescription2 = itemDescription2?.replace("&quot;", "\"")
                itemDescription2 = itemDescription2?.replace("&nbsp;", " ")
                itemDescription2 = itemDescription2?.replace("&ntilde;", "ni")
                tv4b2.text = itemDescription2
                tv4b2.maxWidth = 240
                sv4b1.addView(tv4b2)
                layCustomer.addView(sv4b1)
            }

            //TextView do Rankingu
            val tv5 = TextView(context)
            tv5.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.MATCH_PARENT
            )

            tv5.gravity = Gravity.CENTER
            tv5.setTextColor(Color.parseColor("#000000"))
            tv5.setPadding(10, 10, 10, 10)

            if (i == -1) {
                tv5.text = "Rank"
                tv5.setBackgroundColor(Color.parseColor("#46ACF2"))
                tv5.setTextSize(TypedValue.COMPLEX_UNIT_PX, 28.0f)
            } else {
                tv5.text = row?.currentRank.toString()
                tv5.setBackgroundColor(Color.parseColor("#C3DFEB"))
                tv5.setTextSize(TypedValue.COMPLEX_UNIT_PX, 23.0f)
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
            tr.addView(layCustomer)
            tr.addView(tv5)

            if(i < 0)   tr.setPadding(5, 5, 5, 0)
            else        tr.setPadding(5, 0, 5, 5)

            tableGamesGL.setBackgroundColor(Color.parseColor("#000000"))
            tableGamesGL.addView(tr, trParams)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentGameListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tableGamesGL = requireView().findViewById(R.id.tableGamesGL)

        var allGames = loadDataGL()
        showDataGL(allGames)

        binding.toSUMButtonGL.setOnClickListener {
            findNavController().navigate(R.id.action_gameListFragment_to_summaryFragment)
        }

        binding.bggIDSortButtonGL.setOnClickListener() {
            tableGamesGL.removeAllViews()
            if(flagSortIDGL) {
                allGames = sortDataGL("bggID", "ASC")
                binding.sortTypeTextGL.text = "Sort type: BGG ID ASC"
            }
            else {
                allGames = sortDataGL("bggID", "DESC")
                binding.sortTypeTextGL.text = "Sort type: BGG ID DESC"
            }
            binding.sortTypeTextGL.isVisible = true
            flagSortIDGL = !flagSortIDGL
            showDataGL(allGames)
        }

        binding.rankSortButtonGL.setOnClickListener() {
            tableGamesGL.removeAllViews()
            if(flagSortRankGL) {
                allGames = sortDataGL("currentRank", "ASC")
                binding.sortTypeTextGL.text = "Sort type: RANK ASC"
            }
            else {
                allGames = sortDataGL("currentRank", "DESC")
                binding.sortTypeTextGL.text = "Sort type: RANK DESC"
            }
            binding.sortTypeTextGL.isVisible = true
            flagSortRankGL = !flagSortRankGL
            showDataGL(allGames)
        }

        binding.nameSortButtonGL.setOnClickListener() {
            tableGamesGL.removeAllViews()
            if(flagSortNameGL) {
                allGames = sortDataGL("originalName", "ASC")
                binding.sortTypeTextGL.text = "Sort type: NAME ASC"
            }
            else {
                allGames = sortDataGL("originalName", "DESC")
                binding.sortTypeTextGL.text = "Sort type: NAME DESC"
            }
            binding.sortTypeTextGL.isVisible = true
            flagSortNameGL = !flagSortNameGL
            showDataGL(allGames)
        }

        binding.releaseSortButtonGL.setOnClickListener() {
            tableGamesGL.removeAllViews()
            if(flagSortYearGL) {
                allGames = sortDataGL("releaseYear", "ASC")
                binding.sortTypeTextGL.text = "Sort type: YEAR ASC"
            }
            else {
                allGames = sortDataGL("releaseYear", "DESC")
                binding.sortTypeTextGL.text = "Sort type: YEAR DESC"
            }
            binding.sortTypeTextGL.isVisible = true
            flagSortYearGL = !flagSortYearGL
            showDataGL(allGames)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}