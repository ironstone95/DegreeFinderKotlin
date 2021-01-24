package ai

import csvreaders.IMDBDatabase
import models.Movie
import models.Person

class DegreeFinder(private val source: Person, private val target: Person) {
    private val database = IMDBDatabase.getInstance()!!

    fun findShortestPath(): List<Pair<Movie, Person>>? {
        val targetNode = findTargetNode() ?: return null
        val list = mutableListOf<Pair<Movie, Person>>()
        var curr = targetNode
        while (curr.parent != null) {
            curr.action?.let { list.add(it) }
            curr = curr.parent!!
        }
        return list.reversed()
    }

    private fun findTargetNode(): Node? {
        val frontier = QueueFrontier()
        frontier.add(Node(source, null, null))
        while (!frontier.isEmpty()) {
            val curr = frontier.remove()
            if (curr.source.id == target.id) {
                return curr
            }
            val neighbors = database.getNeighbors(curr.source)
            neighbors.forEach {
                frontier.add(Node(it.second, curr, it))
            }
        }
        return null
    }
}