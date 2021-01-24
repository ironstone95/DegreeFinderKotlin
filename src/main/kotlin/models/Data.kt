package models

sealed class FetchedModel
data class Movie(
    val id: String,
    val title: String,
    val year: String = "",
    val people: MutableSet<String> = mutableSetOf()
) : FetchedModel()

data class Person(
    val id: String,
    val name: String,
    val birth: String = "",
    val movies: MutableSet<String> = mutableSetOf()
) :
    FetchedModel()

data class Star(val personId: String, val movieId: String) : FetchedModel()
