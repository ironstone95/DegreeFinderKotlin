import csvreaders.CSVReader
import org.junit.Test
import kotlin.test.assertEquals

class ReaderTest {
    private val csvReader = CSVReader("large")

    @Test
    fun testLineConverter() {
        val data = "id, \"name one,name three,name four,name two\", year"
        val fetchedList = CSVReader.convertLineToListTest(data)
        println(fetchedList)
        assertEquals(3, fetchedList.size)
    }

    @Test
    fun testCSVMoviesReader() {
        val list = csvReader.getCsvAsListForTest("movies.csv")
        println("Fetched total: ${list.size} items")
        list.forEach {
            assertEquals(3, it.size)
        }
    }

    @Test
    fun testCSVStarsReader() {
        val list = csvReader.getCsvAsListForTest("stars.csv")
        println("Fetched total: ${list.size} items")
        list.forEach {
            assertEquals(2, it.size)
        }
    }

    @Test
    fun testCSVPeopleReader() {
        val list = csvReader.getCsvAsListForTest("people.csv")
        println("Fetched total: ${list.size} items")
        list.forEach {
            assertEquals(3, it.size)
        }
    }

}