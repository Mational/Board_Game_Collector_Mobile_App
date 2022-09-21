package com.example.boardgamecollector

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.example.boardgamecollector.databinding.FragmentSecondBinding
import org.w3c.dom.Document
import org.w3c.dom.NodeList
import java.io.*
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory

class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private var userName: String = ""
    var gamesNumber: Int = 0
    var expansionsNumber: Int = 0
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
    var lastSyncDate = sdf.format(Date())
    var flag: Boolean = false
    var itemsList: MutableList<String> = ArrayList()
    var flagDownload: Boolean = true


    private val binding get() = _binding!!

    @Suppress("DEPRECATED")
    private inner class GameInfoDownloader: AsyncTask<String, Int, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            binding.toSUMwButtonSF.isClickable = true
            binding.toSUMwoButtonSF.isClickable = true
            lastSyncDate = sdf.format(Date())
            saveData()
            findNavController().navigate(R.id.action_SecondFragment_to_summaryFragment)
        }

        override fun doInBackground(vararg p0: String?): String {
            val numberOfFiles = itemsList.size
            var total = 0
            var progress = 0

            for(item in itemsList) {
                try{
                    val url = URL("https://boardgamegeek.com/xmlapi2/thing?id=$item&stats=1")
                    val connection = url.openConnection()
                    connection.connect()
                    val isStream = url.openStream()
                    val appFilesDirectory = context!!.filesDir
                    val testDirectory = File("$appFilesDirectory/XML")
                    if(!testDirectory.exists()) testDirectory.mkdir()
                    val fos = FileOutputStream("$testDirectory/gameInfo.xml")
                    val data = ByteArray(1024)
                    var count = isStream.read(data)
                    while(count != -1) {
                        fos.write(data, 0, count)
                        count = isStream.read(data)
                    }
                    total += 1
                    val progress_temp = total*100 / numberOfFiles
                    progress = progress_temp
                    binding.loadingBarSF.progress = progress
                    isStream.close()
                    fos.close()
                    toDatabase(item.toInt())
                }catch(e: MalformedURLException){
                    Log.i("msg", "Zły URL")
                    continue
                }catch(e: FileNotFoundException){
                    Log.i("msg", "Brak pliku")
                    continue
                }catch(e: IOException){
                    Log.i("msg", "IO Error")
                    continue
                }
            }
            return "success"
        }
    }


    @Suppress("DEPRECATED")
    private inner class AccountInfoDownloader: AsyncTask<String, Int, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            binding.toSUMwoButtonSF.isClickable = false
            binding.toSUMwButtonSF.isClickable = false
            binding.toSUMwoButtonSF.isVisible = false
            binding.toSUMwButtonSF.isVisible = false
            binding.titleTextSF.text = "Data Synchronizing"  //poprawka II
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if(flagDownload && gamesNumber == 0 && expansionsNumber == 0) {
                flagDownload = false
                val dbHandler = MyDBHandler(requireContext(), null, null, 1)
                dbHandler.deleteTable()
                downloadData()
            }else {
                insertDataToDatabase()
                GameInfoDownloader().execute()
            }
        }

        override fun doInBackground(vararg p0: String?): String {
            try{
                val url = URL("https://www.boardgamegeek.com/xmlapi2/collection?username=$userName&stats=1")
                val connection = url.openConnection()
                connection.connect()
                val isStream = url.openStream()
                val appFilesDirectory = context!!.filesDir
                val testDirectory = File("$appFilesDirectory/XML")
                if(!testDirectory.exists()) {
                    testDirectory.mkdir()
                }
                val fos = FileOutputStream("$testDirectory/accountInfo.xml")
                val data = ByteArray(1024)
                var count = 0
                count = isStream.read(data)
                while(count != -1) {
                    fos.write(data, 0, count)
                    count = isStream.read(data)
                }
                isStream.close()
                fos.close()
            }catch(e: MalformedURLException){
                return "Zły URL"
            }catch(e: FileNotFoundException){
                return "Brak pliku"
            }catch(e: IOException){
                return "Wyjątek IO"
            }
            return "success"
        }
    }

    fun toDatabase(itemId: Int) {
        val filename = "gameInfo.xml"
        val path = requireContext().filesDir
        val inDir = File(path, "XML")
        var itemThumbnail: String
        var itemOriginalName: String? = null
        var itemReleaseYear: String
        var itemCurrentRank: Int = 0
        var itemType: String
        var itemDescription: String
        var itemMinPlayers: Int = 0
        var itemMaxPlayers: Int = 0

        if(inDir.exists()) {
            val file = File(inDir, filename)
            if(file.exists()) {
                val xmlDoc: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
                xmlDoc.documentElement.normalize()

                //Pobieranie thumbnail
                var items: NodeList = xmlDoc.getElementsByTagName("thumbnail")
                var itemNode = items.item(0)
                itemThumbnail = itemNode.textContent

                //Pobieranie oryginalnej nazwy
                items = xmlDoc.getElementsByTagName("name")
                for(i in 0 until items.length) {
                    itemNode = items.item(i)
                    if(itemNode.attributes.getNamedItem("type").nodeValue == "primary")
                        itemOriginalName = itemNode.attributes.getNamedItem("value").nodeValue.toString()
                }

                //Pobieranie roku publikacji
                items = xmlDoc.getElementsByTagName("yearpublished")
                itemNode = items.item(0)
                itemReleaseYear = itemNode.attributes.getNamedItem("value").nodeValue

                //Pobieranie typu
                items = xmlDoc.getElementsByTagName("item")
                itemNode = items.item(0)
                itemType = itemNode.attributes.getNamedItem("type").nodeValue
                if(itemType == "boardgame")     gamesNumber++
                else if(itemType == "boardgameexpansion")   expansionsNumber++

                //Pobieranie aktualnej pozycji w rankingu
                items = xmlDoc.getElementsByTagName("rank")
                for(i in 0 until items.length) {
                    itemNode = items.item(i)
                    if(itemNode.attributes.getNamedItem("type").nodeValue == "subtype" &&
                            itemNode.attributes.getNamedItem("name").nodeValue == "boardgame")
                                if(itemNode.attributes.getNamedItem("value").nodeValue != "Not Ranked")
                                    itemCurrentRank = itemNode.attributes.getNamedItem("value").nodeValue.toInt()
                }

                //Pobieranie opisu gry
                items = xmlDoc.getElementsByTagName("description")
                itemNode = items.item(0)
                itemDescription = itemNode.textContent

                //Pobieranie minimalnej liczby graczy
                items = xmlDoc.getElementsByTagName("minplayers")
                itemNode = items.item(0)
                itemMinPlayers = itemNode.attributes.getNamedItem("value").nodeValue.toInt()

                //Pobieranie maksymalnej liczby graczy
                items = xmlDoc.getElementsByTagName("maxplayers")
                itemNode = items.item(0)
                itemMaxPlayers = itemNode.attributes.getNamedItem("value").nodeValue.toInt()



                val dbHandler = MyDBHandler(this.requireContext(), null, null, 1)
                val game = Game(itemId, itemType, itemOriginalName.toString(),
                itemReleaseYear, itemCurrentRank, itemThumbnail, itemDescription, itemMinPlayers, itemMaxPlayers)

                dbHandler.addGame(game)
                dbHandler.addRankGame(game)
                dbHandler.deleteDuplicatesRanks()
                Log.i("msg", "Dodano gre do bazy")
            }
        }
    }

    fun downloadData() {
        val cd = AccountInfoDownloader()
        cd.execute()
    }

    fun insertDataToDatabase() {
        val filename = "accountInfo.xml"
        val path = requireContext().filesDir
        val inDir = File(path, "XML")

        if(inDir.exists()) {
            val file = File(inDir, filename)
            if(file.exists()) {
                val xmlDoc: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)

                xmlDoc.documentElement.normalize()
                val items: NodeList = xmlDoc.getElementsByTagName("item")
                for(i in 0 until items.length) {
                    val itemNode = items.item(i)
                    val itemId = itemNode.attributes.getNamedItem("objectid").nodeValue
                    itemsList.add(itemId)
                }
            }
        }
    }

    fun saveData() {
        val outputData = UserDate(userName, lastSyncDate, gamesNumber.toString(), expansionsNumber.toString())
        val filename = "userdate.csv"
        val file = requireContext().openFileOutput(filename, Context.MODE_PRIVATE)
        file.write(outputData.toCSV().toByteArray())
        file.flush()
        file.close()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)

        //Pobieranie nazwy użytkownika z pierwszego fragmentu
        setFragmentResultListener("FFtoSF") { key, bundle ->
            var temp1 = bundle.getString("userName")
            if(temp1 != null) userName = temp1
        }

        //Pobieranie nazwy użytkownika z trzeciego fragmentu
        setFragmentResultListener("SUMtoSF") { key, bundle ->
            var temp1 = bundle.getString("userName")
            var temp2 = bundle.getString("lastSync")
            if(temp1 != null) userName = temp1
            if(temp2 != null) lastSyncDate = temp2
            binding.lastDateSyncTextView2SF.text = lastSyncDate
            Log.i("msg", userName)
        }

        //SPRAWDZANIE CZY POPRZEDNI FRAGMENT TO SUMMARY CZY CONFIG
        val previousFragment = findNavController().previousBackStackEntry?.destination?.id
        previousFragment?.let {
            when (previousFragment) {
                R.id.summaryFragment -> {
                    binding.toSUMwoButtonSF.isVisible = true
                    binding.toSUMwoButtonSF.isClickable = true
                }
                R.id.FirstFragment -> {
                    binding.toSUMwoButtonSF.isVisible = false
                    binding.toSUMwoButtonSF.isClickable = false
                    binding.lastSyncDateLayoutSF.isVisible = false
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val previousFragment = findNavController().previousBackStackEntry?.destination?.id
        binding.lastDateSyncTextView2SF.text = lastSyncDate

        binding.toSUMwButtonSF.setOnClickListener {
            val nowTime = sdf.format(Date())
            var miliSeconds = Date(nowTime).time - Date(lastSyncDate).time
            miliSeconds /= 1000
            if(previousFragment == R.id.FirstFragment) {
                binding.loadingLayoutSF.isVisible = true
                binding.lastSyncDateLayoutSF.isVisible = false
                downloadData()
            }else{
                if(!flag){
                    if(miliSeconds >= 3600 * 24){
                        binding.loadingLayoutSF.isVisible = true
                        binding.lastSyncDateLayoutSF.isVisible = false
                        downloadData()
                    }
                    else{
                        flag = true
                        binding.toSUMwButtonSF.text = "Yes"
                        binding.toSUMwoButtonSF.text = "No"
                        binding.titleTextSF.text = "Less than 24 hours have passed since the last sync, do you want to start sync anyway?"
                    }
                }
                else if(flag){
                    binding.titleTextSF.text = "Synchronize Window"
                    binding.loadingLayoutSF.isVisible = true
                    binding.lastSyncDateLayoutSF.isVisible = false
                    downloadData()
                }
            }
        }

        binding.toSUMwoButtonSF.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_summaryFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}   