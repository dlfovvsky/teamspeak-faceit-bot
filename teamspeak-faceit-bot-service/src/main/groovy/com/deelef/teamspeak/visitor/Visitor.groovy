package com.deelef.teamspeak.visitor

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

/**
 * // TODO add_java_doc_here
 *
 * @author Adam Gontarek <adam.gontarek@outlook.com>
 * @since 2018/10/18
 */
@Entity
@Table(name = "tb_visitor")
class Visitor {
    @Id
    String nickname

    @Column
    String ip

    @Column
    Date visitDate = new Date()

    String getNickname() {
        return nickname
    }

    void setNickname(String nickname) {
        this.nickname = nickname
    }

    String getIp() {
        return ip
    }

    void setIp(String ip) {
        this.ip = ip
    }

    Date getVisitDate() {
        return visitDate
    }

    void setVisitDate(Date visitDate) {
        this.visitDate = visitDate
    }
}
