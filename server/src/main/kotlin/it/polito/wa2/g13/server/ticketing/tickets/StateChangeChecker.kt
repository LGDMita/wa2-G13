package it.polito.wa2.g13.server.ticketing.tickets

/**
 * Checks that a state change is allowed
 */
fun stateChangeChecker(old: String, new: String, role: String): Boolean {
    return when (old) {
        "open" -> new != "reopened" && !(role != "manager" && new == "in_progress")
        "in_progress" -> new != "reopened"
        "reopened" -> new != "open" && !(role != "manager" && new == "in_progress")
        "resolved" -> (new != "open" && new != "in_progress")
        "closed" -> new == "reopened"
        else -> false
    }
}