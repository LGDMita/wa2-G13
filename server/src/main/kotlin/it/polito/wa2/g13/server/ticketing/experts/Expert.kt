package it.polito.wa2.g13.server.ticketing.experts

import jakarta.persistence.*
import org.springframework.data.util.ProxyUtils
import java.io.Serializable

@Entity
@Table(name= "experts")
class Expert(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
        generator = "expert_generator")
    @SequenceGenerator(name="expert_generator",
        sequenceName = "expert_seq",
        initialValue = 1,
        allocationSize = 1)
    var expertId: Long = 1,
    var name: String,
    var surname: String,
    var sector: String,
    var email: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Expert

        if (email != other.email) return false

        return true
    }

    override fun hashCode(): Int {
        return email.hashCode()
    }

    override fun toString(): String {
        return "Expert(expertId=$expertId, name='$name', surname='$surname', sector='$sector', email='$email')"
    }


}

/*@MappedSuperclass
abstract class ExpertBase<T: Serializable>{
    companion object {
        private const val serialVersionUID = -43869754L
    }

    @Id
    @GeneratedValue
    private var expertId:T?  = null

    fun getId(): T? = expertId

    override fun toString(): String {
        return "@Entity ${this.javaClass.name}(expertId=$expertId)"
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other === this) return true
        if (javaClass != ProxyUtils.getUserClass(other))
            return false
        other as ExpertBase<*>
        return  if (null == expertId) false
                else this.expertId == other.expertId
    }

    override fun hashCode(): Int {
        return 31 //any value will do
    }

}*/

fun ExpertDTO.toExpert(): Expert {
    return Expert(expertId, name, surname, sector, email)
}