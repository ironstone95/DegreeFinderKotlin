package observer

interface Publisher<T> {
    fun addObserver(observer: Observer<T>)

    fun notifyObservers()
}