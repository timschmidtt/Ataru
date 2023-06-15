package de.tsm.ataru.model.user

import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class UserRepository @Autowired constructor(
    private val entityManager: EntityManager
) {

    fun get(userId: UserId): User = entityManager
        .createQuery("SELECT u FROM User u WHERE u.id = :uId", User::class.java)
        .setParameter("uId", userId)
        .singleResult

    fun list(): List<User> = entityManager
        .createQuery("SELECT u FROM User u", User::class.java)
        .resultList

    @Transactional
    fun create(
        login: Email,
        password: Password,
        userName: UserName,
        firstname: String,
        lastname: String,
        nutrition: Nutrition?
    ): User {
        val user = User(login, password, userName, firstname, lastname, nutrition)
        entityManager.persist(user)
        return user
    }
}