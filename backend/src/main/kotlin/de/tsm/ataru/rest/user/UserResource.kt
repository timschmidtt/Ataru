package de.tsm.ataru.rest.user

import de.tsm.ataru.model.user.*
import org.jetbrains.annotations.NotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserResource @Autowired constructor(
    private val userRepository: UserRepository,
) {

    @GetMapping("/")
    fun list(): UserListResponse {
        val users = userRepository.list()
        return UserListResponse(
            users.map(::UserResponse)
        )
    }

    @GetMapping("/{userId}")
    fun get(@PathVariable userId: String): UserResponse =
        userRepository.get(UserId(userId)).let(::UserResponse)

    @PostMapping("/")
    fun create(@NotNull @RequestBody rup: RegisterUserParam): UserResponse {
        // registerUserParam.validate()
        val user = userRepository.create(
            Email(rup.login),
            Password(PlaintextPassword(rup.password)),
            UserName(rup.username),
            rup.firstName,
            rup.lastName,
            rup.nutrition?.let { Nutrition.valueOf(it) }
        )
        return UserResponse(user)
    }
}

data class UserResponse(
    val id: String,
    val email: String,
    val userName: String,
    val firstname: String,
    val lastname: String,
    val nutrition: Nutrition?
) {
    constructor(user: User) : this(
        user.userId.value,
        user.login.value,
        user.userName.value,
        user.firstName,
        user.lastName,
        user.nutrition
    )
}

data class UserListResponse(val users: List<UserResponse>)

data class RegisterUserParam(
    val login: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val username: String,
    val nutrition: String?
)
