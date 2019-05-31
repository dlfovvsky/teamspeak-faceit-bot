package com.deelef.teamspeak.domain.repository


import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * Created by adam on 16.03.2017.
 */
@Repository
interface UserRepository extends JpaRepository<com.deelef.teamspeak.domain.model.User, Long>{
    Optional<com.deelef.teamspeak.domain.model.User> findOneBySteam64Id(String steam64Id);

    @Query("SELECT u FROM User u WHERE u.faceitClient.nickname IS NOT NULL")
    List<com.deelef.teamspeak.domain.model.User> findFaceitClients();

    @Query("SELECT u FROM User u WHERE u.faceitClient.nickname IS NOT NULL and u.faceitClient.playing=TRUE")
    List<com.deelef.teamspeak.domain.model.User> findPlayingFaceitClients()
}
