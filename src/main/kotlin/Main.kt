import cli.CLI
import csvreaders.CSVReader
import csvreaders.IMDBOrganizer
import enums.ReadStatus
import kotlinx.coroutines.runBlocking

private lateinit var csvReader: CSVReader
private lateinit var organizer: IMDBOrganizer

fun main() = runBlocking {
    readFiles()
}

suspend fun readFiles() {
    csvReader = CSVReader("large")
    csvReader.addObserver {
        if (it == ReadStatus.READING) {
            print("Reading Data.")
        }
        if (it == ReadStatus.SUCCESS) {
            println()
            runBlocking {
                organizeFiles()
            }
        }
    }
    csvReader.readFiles().join()
}

suspend fun organizeFiles() {
    if (csvReader.movies != null && csvReader.people != null && csvReader.stars != null && csvReader.names != null) {
        organizer = IMDBOrganizer(csvReader.movies!!, csvReader.people!!, csvReader.stars!!, csvReader.names!!)
        organizer.addObserver {
            if (it == ReadStatus.READING) {
                print("Organizing Data")
            }
            if (it == ReadStatus.SUCCESS) {
                println("\n")
                if (organizer.createDataHolder()) {
                    CLI().start()
                }

            }
        }
        organizer.organizeData().join()
    }
}









