package it.polito.wa2.g13.server.ticketing.tickets

fun stateChangeChecker(old: String, new: String): Boolean {
    return when(old){
        "open" -> new != "reopened"
        "in_progress" -> new != "reopened"
        "reopened" -> new != "open"
        "resolved" -> (new != "open" && new != "in_progress")
        "closed" -> new == "reopened"
        else -> false
    }
}