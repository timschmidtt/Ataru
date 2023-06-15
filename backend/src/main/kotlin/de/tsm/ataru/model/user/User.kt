package de.tsm.ataru.model.user

import de.cewe.deskstar.util.CryptoHelper
import de.cewe.deskstar.util.IdGenerator
import de.tsm.ataru.model.user.Nutrition.*
import de.tsm.ataru.model.user.Password.Companion.buildPasswordValue
import de.tsm.ataru.model.user.Password.Companion.createNewSalt
import de.tsm.ataru.model.user.Password.Companion.hashWithVariant1
import jakarta.persistence.*
import java.io.Serializable
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

@Entity
@Table(name = "user")
class User(

    @AttributeOverride(name = "value", column = Column(name = "email", nullable = false, unique = true))
    val login: Email,

    @AttributeOverride(name = "value", column = Column(name = "password", nullable = false))
    val password: Password,

    @AttributeOverride(name = "value", column = Column(name = "user_name", nullable = false))
    val userName: UserName,

    @Column(name = "first_name")
    val firstName: String,

    @Column(name = "last_name")
    val lastName: String,

    @Column(name = "nutrition")
    @Enumerated(EnumType.STRING)
    val nutrition: Nutrition? = OMNIVORE,

    @Id
    @AttributeOverride(name = "id", column = Column(name = "userId"))
    val userId: UserId = UserId(),
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false
        return userId == other.userId
    }

    override fun hashCode(): Int {
        return userId.hashCode()
    }

    override fun toString(): String {
        return "User(userId=$userId, login=$login, password=$password, userName='$userName', firstName='$firstName', lastName='$lastName')"
    }
}

@Embeddable
data class UserId(val value: String = IdGenerator.next()) : Serializable

@Embeddable
data class Email(val value: String) {
    init {
        require(value.matches("""[^@]+@.+\.\w+""".toRegex()))
    }
}

data class PlaintextPassword(val value: String)

@Embeddable
data class Password(val value: String) {

    /**
     * Compares the hashed password to the plain text password entered by the user. Use always this function, don't use
     * [.equals].
     *
     * @param plainTextPassword
     * @return `true` if the plain text password matches, otherwise `false`
    */
    fun isEqualToPlainPassword(plainTextPassword: PlaintextPassword): Boolean {
        val parts = value.split(PARTS_SPLIT)
        val salt = CryptoHelper.base64ToByte(parts[1])
        val hash: ByteArray = hashWithVariant1(plainTextPassword.value, salt)
        val hashPasswordToCompare = CryptoHelper.byteToBase64(hash)
        return hashPasswordToCompare == parts[2]
    }

    companion object {
        // must NOT be in the base64 charset
        internal val PARTS_SPLIT = "$"
        internal val VARIANT_1 = "v1"

        /*
         * Creates a 64 bits long salt.
         */
        internal fun createNewSalt(): ByteArray {
            val bSalt = ByteArray(8)
            CryptoHelper.nextSecureBytes(bSalt)
            return bSalt
        }

        // has no extra null checks on the input parameter!
        internal fun hashWithVariant1(plainTextPassword: String, salt: ByteArray): ByteArray {
            /*
           * The following configuration MUST NOT BE CHANGED, because there are existing passwords with this variant and
           * this exactly this configuration.
           * However, you can create a new variant with a modified configuration.
           */
            val algorithm = "PBKDF2WithHmacSHA1"
            val iterationCount = 16000
            val keySize = 256
            val spec = PBEKeySpec(plainTextPassword.toCharArray(), salt, iterationCount, keySize)
            return try {
                val factory = SecretKeyFactory.getInstance(algorithm)
                factory.generateSecret(spec).encoded
            } catch (e: InvalidKeySpecException) {
                throw RuntimeException(e.message, e)
            } catch (e: NoSuchAlgorithmException) {
                throw RuntimeException(e.message, e)
            }
        }

        internal fun buildPasswordValue(variant: String, salt: String, hash: String): String {
            return variant + PARTS_SPLIT + salt + PARTS_SPLIT + hash
        }
    }
}

fun Password(plainTextPassword: PlaintextPassword): Password {
    val salt = createNewSalt()
    val hash = hashWithVariant1(plainTextPassword.value, salt)
    return Password(buildPasswordValue(Password.VARIANT_1, CryptoHelper.byteToBase64(salt), CryptoHelper.byteToBase64(hash)))
}

@Embeddable
data class UserName(val value: String)

enum class Nutrition {
    OMNIVORE,
    FLEXITARIAN,
    PESCETARIAN,
    VEGETARIAN,
    VEGAN
}
