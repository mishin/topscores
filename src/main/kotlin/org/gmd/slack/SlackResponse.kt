package org.gmd.slack

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper

class SlackResponse(val text: String = "Something went wrong!",
                    @get:JsonProperty("response_type")
                    val responseType: String = "ephemeral",
                    val attachments: List<SlackAttachment> = emptyList()) {

    fun toJson(): String {
        return ObjectMapper().writeValueAsString(this)
    }
}

