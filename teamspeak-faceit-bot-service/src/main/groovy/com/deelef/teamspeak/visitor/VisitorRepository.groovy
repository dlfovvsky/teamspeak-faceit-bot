package com.deelef.teamspeak.visitor

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Created by adam on 16.03.2017.
 */
@Repository
interface VisitorRepository extends JpaRepository<Visitor, String>{

}
