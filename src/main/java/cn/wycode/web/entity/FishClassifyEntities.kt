package cn.wycode.web.entity

data class ClassifyResult(
        val result: List<ResultItem> = ArrayList()
)


data class ResultItem(
        val score: Float = 0f,
        val name: String = "",
        val baike_info: BaikeInfo? = null
)


data class BaikeInfo(
        val image_url: String? = null,
        val description: String? = null
)