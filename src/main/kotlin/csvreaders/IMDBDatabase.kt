package csvreaders

import models.Movie
import models.Person

class IMDBDatabase private constructor(
    private val movieMap: Map<String, Movie>,
    private val peopleMap: Map<String, Person>,
    private val nameMap: Map<String, List<String>>
) {

    fun getPeopleWithName(name: String): List<Person> {
        val list = mutableListOf<Person>()
        nameMap[name]?.forEach { id ->
            list.add(peopleMap[id]!!)
        }
        return list
    }

    fun getNeighbors(person: Person): Set<Pair<Movie, Person>> {
        val movieIds = peopleMap[person.id]?.movies
        val peopleSet = mutableSetOf<Pair<Movie, Person>>()
        if (movieIds == null) {
            return peopleSet
        }
        movieIds.forEach { movieId ->
            val movie = movieMap[movieId]
            movie?.people?.forEach { personId ->
                if (personId != person.id) {
                    peopleMap[personId]?.let { currPerson ->
                        val pair = Pair(movie, currPerson)
                        peopleSet.add(pair)
                    }
                }
            }
        }
        return peopleSet
    }

    companion object {
        var INSTANCE: IMDBDatabase? = null

        fun createInstance(
            movieMap: Map<String, Movie>,
            peopleMap: Map<String, Person>,
            nameMap: Map<String, List<String>>
        ): Boolean {
            if (INSTANCE == null) {
                INSTANCE = IMDBDatabase(movieMap, peopleMap, nameMap)
                return true
            }
            return false
        }

        fun getInstance(): IMDBDatabase? {
            return INSTANCE
        }
    }
}