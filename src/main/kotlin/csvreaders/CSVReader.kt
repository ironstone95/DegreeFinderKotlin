package csvreaders

import enums.ReadStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import models.Movie
import models.Person
import models.Star
import observer.Observer
import observer.Publisher
import org.jetbrains.annotations.TestOnly
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import kotlin.system.exitProcess

/**
 * This class produces raw data.
 */
class CSVReader(private val dirName: String) : Publisher<ReadStatus> {
    private var status = ReadStatus.NOT_STARTED
        set(value) {
            val publish = value != field
            field = value
            if (publish) {
                notifyObservers()
            }
        }
    private val observers = mutableSetOf<Observer<ReadStatus>>()

    private var movieMap = mutableMapOf<String, Movie>()
    private var peopleMap = mutableMapOf<String, Person>()
    private var starMap = mutableListOf<Star>()
    private var nameMap = mutableMapOf<String, MutableList<String>>()

    var movies: Map<String, Movie>? = null
    var people: Map<String, Person>? = null
    var stars: List<Star>? = null
    var names: Map<String, List<String>>? = null

    override fun notifyObservers() {
        observers.forEach {
            it.onUpdate(data = status)
        }
    }

    suspend fun readFiles() = withContext(Dispatchers.IO) {
        status = ReadStatus.READING
        launch {
            readMoviesCSV()
        }
        launch {
            readPeopleCSV()
        }
        launch {
            readStarsCSV()
        }
    }

    private fun readFinished() = movies != null && people != null && stars != null && names != null

    private fun readMoviesCSV() {
        readCSVFile(
            doOnStart = null,
            "movies.csv",
            doOnEachLine = {
                val movie = Movie(it[0], it[1], it[2])
                movieMap[movie.id] = movie
            },
            doOnEnd = {
                movies = movieMap
            }
        )
    }

    private fun readPeopleCSV() {
        readCSVFile(
            doOnStart = null,
            "people.csv",
            doOnEachLine = {
                val name = it[1].removeSurrounding("\"")
                val person = Person(it[0], name, it[2])
                peopleMap[person.id] = person
                nameMap.putIfAbsent(name, mutableListOf())
                nameMap[name]?.add(it[0])
            },
            doOnEnd = {
                people = peopleMap
                names = nameMap
            }
        )
    }

    private fun readStarsCSV() {
        readCSVFile(
            doOnStart = null,
            "stars.csv",
            doOnEachLine = {
                val star = Star(it[0], it[1])
                starMap.add(star)
            },
            doOnEnd = {
                stars = starMap
            }
        )
    }

    private fun readCSVFile(
        doOnStart: (() -> Unit)?,
        fileName: String,
        doOnEachLine: (List<String>) -> Unit,
        doOnEnd: (() -> Unit)?
    ) {
        val file = File("./$dirName/$fileName")
        doOnStart?.let { it() }
        FileInputStream(file).use { fis ->
            InputStreamReader(fis).use { isr ->
                isr.forEachLine {
                    try {
                        val list = convertLineToList(it)
                        doOnEachLine(list)
                    } catch (e: IOException) {
                        exitProcess(-1)
                    }
                }
            }
        }
        doOnEnd?.let { it() }
        if (readFinished()) {
            status = ReadStatus.SUCCESS
        }
    }

    @TestOnly
    fun getCsvAsListForTest(fileName: String): List<List<String>> {
        val dataList = mutableListOf<List<String>>()
        readCSVFile(
            doOnStart = null,
            fileName,
            doOnEachLine = {
                dataList.add(it)
            },
            doOnEnd = null
        )
        return dataList
    }

    companion object {
        @TestOnly
        fun convertLineToListTest(line: String): List<String> {
            return convertLineToList(line)
        }

        private fun convertLineToList(line: String): List<String> {
            val list = line.split(",").toMutableList()
            val quoteIndexes = mutableListOf<Int>()
            list.forEachIndexed { index, it ->
                if (it.contains("\"")) {
                    quoteIndexes.add(index)
                }
            }
            while (quoteIndexes.size > 1) {
                val endIndex = quoteIndexes.removeLast()
                val startIndex = quoteIndexes.removeLast()

                var data = ""
                for (i in endIndex downTo (startIndex + 1)) {
                    data += " " + list[i]
                    list.removeAt(i)
                }
                list[startIndex] += data
                list[startIndex] = list[startIndex].replace('"', ' ')
            }
            list.forEachIndexed { i, str ->
                list[i] = str.replace(Regex("[ ]{2,}"), " ")
                list[i] = list[i].trim()
            }
            return list
        }
    }

    override fun addObserver(observer: Observer<ReadStatus>) {
        observer.onUpdate(status)
        observers.add(observer)
    }
}