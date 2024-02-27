package github.com.st235.facialprocessing.utils

open class Observable<T> {

    private val lock = Any()
    private val observables = mutableListOf<T>()

    fun addCallback(observable: T) {
        synchronized(lock) {
            observables.add(observable)
        }
    }

    fun removeCallback(observable: T) {
        synchronized(lock) {
            observables.remove(observable)
        }
    }

    protected fun notifyCallbacks(predicate: (observable: T) -> Unit) {
        synchronized(lock) {
            for (observable in observables) {
                predicate(observable)
            }
        }
    }

}