package ai

import models.Movie
import models.Person

data class Node(val source: Person, val parent: Node?, val action: Pair<Movie, Person>?)