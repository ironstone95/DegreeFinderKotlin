package cli

import ai.DegreeFinder
import csvreaders.IMDBDatabase
import models.Person

class CLI {
    private val database = IMDBDatabase.getInstance()!!

    fun start() {
        println("Welcome to Star Degree Finder!")

        main@ while (true) {
            showCommands()
            val input = readLine()
            if (input == "1") {
                var source: Person? = null
                while (source == null) {
                    println("Enter source name:")
                    val input2 = readLine() ?: continue
                    if (input2 == "0") {
                        continue@main
                    }
                    source = getPerson(input2)
                    if (source == null) {
                        println("Person with $input2 not found. Try Again. To Quit Type 0.")
                    }
                }
                var target: Person? = null
                while (target == null) {
                    println("Enter target name: ")
                    val input2 = readLine() ?: continue
                    if (input == "0") {
                        continue@main
                    }
                    target = getPerson(input2)
                    if (target == null) {
                        println("Person with $input2 not found. Try Again. To Quit Type 0.")
                    }
                }
                if (source == null || target == null) {
                    continue
                }
                printFoundPath(source, target)
            } else if (input == "0") {
                break
            } else {
                println("Unknown Input")
                continue
            }
        }
    }

    private fun printFoundPath(source: Person, target: Person) {
        val finder = DegreeFinder(source, target)
        val shortestPath = finder.findShortestPath()
        if (shortestPath == null) {
            println("No connection found.")
            return
        }
        println("Found degree: ${shortestPath.size}")
        var personLeft = source
        shortestPath.forEachIndexed { index, action ->
            println("${index + 1} - ${personLeft.name} starred in ${action.first.title} with ${action.second.name}")
            personLeft = action.second
        }
    }

    private fun getPerson(name: String): Person? {
        val peopleList = database.getPeopleWithName(name)
        return when {
            peopleList.size > 1 -> {
                getPerson(name, peopleList)
            }
            peopleList.size == 1 -> {
                peopleList[0]
            }
            else -> {
                null
            }
        }
    }

    private fun getPerson(name: String, list: List<Person>): Person {
        println("There are more than one person with name $name. Please Enter Index Of The Person")
        var input: Int
        while (true) {
            list.forEachIndexed { index, person ->
                println("${index + 1} - $name, Born At: ${person.birth}")
            }
            try {
                input = readLine()?.toInt() ?: continue
                break
            } catch (e: NumberFormatException) {
                println("Please Enter Only Number")
            }
        }
        return when {
            input - 1 < 0 -> {
                list[0]
            }
            input - 1 >= list.size -> {
                list.last()
            }
            else -> {
                list[input - 1]
            }
        }
    }

    private fun showCommands() {
        println("---> Command Menu <----\n1 - Search\n0 - Exit")
    }
}