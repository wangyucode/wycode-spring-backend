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

