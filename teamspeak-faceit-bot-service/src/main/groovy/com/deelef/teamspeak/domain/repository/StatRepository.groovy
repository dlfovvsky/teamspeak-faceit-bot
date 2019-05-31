package com.deelef.teamspeak.domain.repository


import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Created by adam on 16.03.2017.
 */
@Repository
interface StatRepository extends JpaRepository<com.deelef.teamspeak.domain.model.Stat, Long>{

}
