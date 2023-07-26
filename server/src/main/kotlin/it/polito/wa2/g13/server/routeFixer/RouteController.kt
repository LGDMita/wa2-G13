package it.polito.wa2.g13.server.routeFixer

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class RouteController {

    @GetMapping("/{path:[^.]*}")
    fun redirectSingle(): String {
        return "forward:/"
    }

    @GetMapping("/*/{path:[^.]*}")
    fun redirectNested(@PathVariable path: String): String {
        return "forward:/"
    }
}
