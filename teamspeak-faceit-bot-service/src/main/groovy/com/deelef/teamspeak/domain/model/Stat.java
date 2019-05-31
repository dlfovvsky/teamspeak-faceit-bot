package com.deelef.teamspeak.domain.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * TODO add comment here!!!
 *
 * @author Adam Gontarek <adam.gontarek@outlook.com>
 * @since 17/06/2017
 */
@Entity
@Table(name = "tb_stat")
public class Stat implements Serializable {

    @Id
    Long id;

    @Column
    Long hit;

    public Stat() {
    }

    public Long getId() {
        return id;
    }

    public Long getHit() {
        return hit;
    }
}
