package de.cewe.deskstar.util

import java.util.UUID

object IdGenerator {

  fun next(): String = UUID.randomUUID().toString().replace("-", "")

}
