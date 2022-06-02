package com.example.boardgamecollector

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
import com.example.boardgamecollector.databinding.FragmentDlcListBinding
import java.net.URL

class DlcListFragment : Fragment() {

    private var _binding: FragmentDlcListBinding? = null
    private val binding get() = _binding!!
    private lateinit var tableGamesDL: TableLayout

    var flagSortIDDL: Boolean = true
    var flagSortNameDL: Boolean = true
    var flagSortYearDL: Boolean = true

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
        val result = dbHandler.findGames("boardgameexpansion")
        return result
    }

    private fun sortDataGL(sCol: String, direct: String): MutableList<Game> {
        val dbHandler = MyDBHandler(this.requireContext(), null, null, 1)
        val result = dbHandler.sortGames("boardgameexpansion", direct, sCol)
        return result
    }

    private fun showDataGL(games: MutableList<Game>) {
        val leftRowMargin = 0
        val topRowMargin = 0
        val rightRowMargin = 0
        val bottomRowMargin = 0

        val rows = games.size
        var textSpacer: TextView? = null

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
            tv4a.maxWidth = 280

            if (i == -1) {
                tv4a.text = "Informations"
                tv4a.setBackgroundColor(Color.parseColor("#46ACF2"))
                tv4a.setTextColor(Color.parseColor("#000000"))
                tv4a.setPadding(10, 10, 10, 10)
                tv4a.setTextSize(TypedValue.COMPLEX_UNIT_PX, 28.0f)
            } else {
                tv4a.setTextColor(Color.parseColor("#0000ff"))
                tv4a.setBackgroundColor(Color.parseColor("#C3DFEB"))
                tv4a.text = "${row?.originalTitle}\n(${row?.releaseYear})"
                tv4a.setPadding(5, 5, 5, 5)
                tv4a.setTextSize(TypedValue.COMPLEX_UNIT_PX, 20.0f)
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
                    TableRow.LayoutParams.WRAP_CONTENT
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
                tv4b2.maxWidth = 280
                sv4b1.addView(tv4b2)
                layCustomer.addView(sv4b1)
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

            if(i < 0)   tr.setPadding(5, 5, 5, 0)
            else        tr.setPadding(5, 0, 5, 5)

            tableGamesDL.setBackgroundColor(Color.parseColor("#000000"))
            tableGamesDL.addView(tr, trParams)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDlcListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tableGamesDL = requireView().findViewById(R.id.tableExpensionDL)

        var allGames = loadDataGL()
        showDataGL(allGames)

        binding.toSUMfromDL.setOnClickListener {
            findNavController().navigate(R.id.action_dlcListFragment_to_summaryFragment)
        }

        binding.idSortButtonDL.setOnClickListener() {
            tableGamesDL.removeAllViews()
            if(flagSortIDDL) {
                allGames = sortDataGL("bggID", "ASC")
                binding.sortTextViewDL.text = "Sort type: BGG ID ASC"
            }
            else {
                allGames = sortDataGL("bggID", "DESC")
                binding.sortTextViewDL.text = "Sort type: BGG ID DESC"
            }
            binding.sortTextViewDL.isVisible = true
            flagSortIDDL = !flagSortIDDL
            showDataGL(allGames)
        }

        binding.nameSortButtonDL.setOnClickListener() {
            tableGamesDL.removeAllViews()
            if(flagSortNameDL) {
                allGames = sortDataGL("originalName", "ASC")
                binding.sortTextViewDL.text = "Sort type: NAME ASC"
            }
            else {
                allGames = sortDataGL("originalName", "DESC")
                binding.sortTextViewDL.text = "Sort type: NAME DESC"
            }
            binding.sortTextViewDL.isVisible = true
            flagSortNameDL = !flagSortNameDL
            showDataGL(allGames)
        }

        binding.yearSortButtonDL.setOnClickListener() {
            tableGamesDL.removeAllViews()
            if(flagSortYearDL) {
                allGames = sortDataGL("releaseYear", "ASC")
                binding.sortTextViewDL.text = "Sort type: YEAR ASC"
            }
            else {
                allGames = sortDataGL("releaseYear", "DESC")
                binding.sortTextViewDL.text = "Sort type: YEAR DESC"
            }
            binding.sortTextViewDL.isVisible = true
            flagSortYearDL = !flagSortYearDL
            showDataGL(allGames)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}