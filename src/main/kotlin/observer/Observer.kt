package observer

fun interface Observer<T> {
    fun onUpdate(data: T)
}