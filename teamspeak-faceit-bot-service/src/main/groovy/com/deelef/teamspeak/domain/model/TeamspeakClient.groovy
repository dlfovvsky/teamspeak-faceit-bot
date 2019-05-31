package com.deelef.teamspeak.domain.model

import javax.persistence.Embeddable
import javax.persistence.Transient

/**
 * Created by adam on 16.03.2017.
 */
@Embeddable
class TeamspeakClient {
    int databaseId
    String uid

    @Transient
    Boolean online


}
