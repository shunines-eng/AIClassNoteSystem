package com.noteaiBackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "role_user")
@Data
@IdClass(RoleUserId.class)
public class RoleUser {
    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Id
    @Column(name = "role_id")
    private Integer roleId;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class RoleUserId implements Serializable {
    private Integer userId;
    private Integer roleId;
}
