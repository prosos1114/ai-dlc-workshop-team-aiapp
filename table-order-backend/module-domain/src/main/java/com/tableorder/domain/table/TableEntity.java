package com.tableorder.domain.table;

import com.tableorder.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tables", uniqueConstraints = @UniqueConstraint(columnNames = {"storeId", "tableNumber"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TableEntity extends BaseEntity {

    @Column(nullable = false)
    private Long storeId;

    @Column(nullable = false)
    private Integer tableNumber;

    @Column(nullable = false)
    private String password;

    @Builder
    public TableEntity(Long storeId, Integer tableNumber, String password) {
        this.storeId = storeId;
        this.tableNumber = tableNumber;
        this.password = password;
    }

    public void updatePassword(String password) {
        this.password = password;
    }
}
