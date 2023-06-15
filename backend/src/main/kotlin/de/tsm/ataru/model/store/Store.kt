package de.tsm.ataru.model.store

import de.cewe.deskstar.util.IdGenerator
import jakarta.persistence.*
import java.io.Serializable

@Entity
@Table(name = "store")
class Store(

    @Id
    @AttributeOverride(name = "id", column = Column(name = "storeId"))
    val storeId: StoreId = StoreId(),

    @Column(name = "storeName")
    val storeName: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Store) return false
        return storeId == other.storeId
    }

    override fun hashCode(): Int {
        return storeId.hashCode()
    }

    override fun toString(): String {
        return "User(userId=$storeId)"
    }
}

@Embeddable
data class StoreId(val id: String = IdGenerator.next()) : Serializable
