package imin.jeju.iminjeju.extentions


val tag_regex = Regex("<[^>]*>", RegexOption.IGNORE_CASE)

fun String.trimTags(): String {
    return this.replace(tag_regex, "")
}