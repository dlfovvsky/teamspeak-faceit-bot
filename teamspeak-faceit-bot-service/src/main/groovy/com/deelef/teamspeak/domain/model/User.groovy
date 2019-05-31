package com.deelef.teamspeak.domain.model


import javax.persistence.Column
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

/**
 * Created by adam on 16.03.2017.
 */

@Entity
@Table(name = "tb_user")
public class User implements Serializable{
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    Long id;

    @Column
    String steam64Id;

    @Embedded
    TeamspeakClient teamspeakClient;

    @Embedded
    FaceitClient faceitClient

    @Embedded
    SteamInfo steamInfo

    @Column
    Boolean autoRegister



    User() {
    }

}
