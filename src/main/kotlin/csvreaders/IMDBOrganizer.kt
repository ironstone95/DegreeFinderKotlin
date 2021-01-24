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

class IMDBOrganizer(
    private val movieMap: Map<String, Movie>,
    private val peopleMap: Map<String, Person>,
    private val rawStarList: List<Star>,
    private val nameMap: Map<String, List<String>>
) : Publisher<ReadStatus> {
    private var status = ReadStatus.NOT_STARTED
        set(value) {
            val changed = value != field
            field = value
            if (changed) {
                notifyObservers()
            }
        }
    private val observers = mutableSetOf<Observer<ReadStatus>>()

    suspend fun organizeData() = withContext(Dispatchers.Default) {
        launch {
            status = ReadStatus.READING
            rawStarList.forEach { star ->
                val person = peopleMap[star.personId]
                person?.movies?.add(star.movieId)
                val movie = movieMap[star.movieId]
                movie?.people?.add(star.personId)
            }
            status = ReadStatus.SUCCESS
        }
    }

    fun createDataHolder(): Boolean {
        return IMDBDatabase.createInstance(movieMap, peopleMap, nameMap)
    }

    override fun addObserver(observer: Observer<ReadStatus>) {
        observer.onUpdate(status)
        observers.add(observer)
    }

    override fun notifyObservers() {
        observers.forEach { it.onUpdate(status) }
    }
}