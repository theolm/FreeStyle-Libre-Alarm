package dev.theolm.freestyle_libre_alarm.domain.util

data class SemVersion(
    val major: Int,
    val minor: Int,
    val patch: Int
) : Comparable<SemVersion> {

    override fun compareTo(other: SemVersion): Int {
        return compareValuesBy(
            this, other,
            { it.major },
            { it.minor },
            { it.patch }
        )
    }

    override fun toString(): String = "$major.$minor.$patch"
}

object SemVerParser {

    private val SEMVER_REGEX = Regex("""
        ^v?(\d+)\.(\d+)\.(\d+)$
    """.trimIndent())

    fun parse(versionString: String): SemVersion? {
        val match = SEMVER_REGEX.matchEntire(versionString.trim())
            ?: return null

        val (major, minor, patch) = match.destructured
        return SemVersion(
            major = major.toInt(),
            minor = minor.toInt(),
            patch = patch.toInt()
        )
    }

    fun isNewer(currentVersion: String, candidateVersion: String): Boolean {
        val current = parse(currentVersion)
            ?: throw IllegalArgumentException("Invalid current version: $currentVersion")
        val candidate = parse(candidateVersion)
            ?: throw IllegalArgumentException("Invalid candidate version: $candidateVersion")

        return candidate > current
    }
}
