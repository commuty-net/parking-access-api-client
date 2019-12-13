package net.commuty.parking

import net.commuty.parking.configuration.JsonMapper

trait RestTrait {

    String host = "http://localhost:8080"
    JsonMapper mapper = JsonMapper.create()
}