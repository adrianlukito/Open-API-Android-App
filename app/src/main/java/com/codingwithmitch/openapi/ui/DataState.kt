package com.codingwithmitch.openapi.ui

data class DataState<T>(
    val error: Event<StateError>? = null,
    var loading: Loading = Loading(false),
    var data: Data<T>? = null
) {
    companion object {
        fun <T> error (response: Response): DataState<T> {
            return DataState(error = Event(StateError(response)))
        }

        fun <T> loading(isLoading: Boolean, cachedData: T?): DataState<T> {
            return DataState(
                loading = Loading(isLoading),
                data = Data(Event.dataEvent(cachedData), null)
            )
        }

        fun <T> success(data: T?, response: Response?): DataState<T> {
            return DataState(data = Data(Event.dataEvent(data), Event.responseEvent(response)))
        }
    }
}