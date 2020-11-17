package cn.wycode.web.entity

data class JsonResult<T> private constructor(val data: T? = null, val success: Boolean = false, val error: String? = null) {

    companion object {
        fun <T> data(data: T?): JsonResult<T> {
            return JsonResult(data, true, null)
        }

        fun <T> error(error: String?): JsonResult<T> {
            return JsonResult(null, false, error)
        }
    }
}


data class CommonMessage<T>(val data: T?, val error: String?) {
    companion object {
        fun <T> success(data: T): CommonMessage<T> {
            return CommonMessage(data, null)
        }

        fun fail(error: String): CommonMessage<Nothing> {
            return CommonMessage(null, error)
        }
    }
}

