package ai

import java.util.*

class QueueFrontier {
    private val frontier: Queue<Node> = ArrayDeque()
    private val explored: MutableSet<String> = mutableSetOf()

    fun add(node: Node) {
        if (!explored.contains(node.source.id)) {
            frontier.add(node)
        }
    }

    fun remove(): Node {
        val node = frontier.poll()
        explored.add(node.source.id)
        return node
    }

    fun isEmpty() = frontier.isEmpty()
}