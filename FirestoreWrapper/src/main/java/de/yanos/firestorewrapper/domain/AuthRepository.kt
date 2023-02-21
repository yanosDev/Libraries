package de.yanos.firestorewrapper.domain

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

data class AuthConfig(var debugVerification: Boolean = false, var dispatcher: CoroutineDispatcher = Dispatchers.IO)

interface AuthRepositoryBuilder {
    fun enableDebugVerification()
    fun disableDebugVerification()
    fun build(): AuthRepository

    companion object {
        fun Builder(): AuthRepositoryBuilder {
            return AuthRepositoryBuilderImpl()
        }
    }
}

internal class AuthRepositoryBuilderImpl : AuthRepositoryBuilder {
    private val config = AuthConfig()
    override fun enableDebugVerification() {
        config.debugVerification = true
    }

    override fun disableDebugVerification() {
        config.debugVerification = false
    }

    override fun build(): AuthRepository {
        return AuthRepositoryImpl(config)
    }
}

interface AuthRepository {
    suspend fun signInAnonymously(): AuthResult
    suspend fun switchAnonymousToPassword(email: String, password: String): AuthResult
    suspend fun switchAnonymousToGoogle(idToken: String): AuthResult
    suspend fun loginPasswordUser(email: String, password: String): AuthResult
    suspend fun loginGoogle(email: String, password: String): AuthResult
}

internal class AuthRepositoryImpl(config: AuthConfig) : AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val dispatcher = config.dispatcher

    init {
        auth.firebaseAuthSettings
            .setAppVerificationDisabledForTesting(!config.debugVerification)
    }

    override suspend fun signInAnonymously(): AuthResult {
        return withContext(dispatcher) {
            auth.signInAnonymously().await()?.let { authResult ->
                authResult.user?.let { user ->
                    AuthResult.User(
                        id = user.uid,
                        email = user.email ?: "",
                        name = user.displayName ?: "",
                        provider = authResult.credential?.provider ?: ""
                    )
                }
            } ?: AuthResult.Failure("Anonymous login failed")
        }
    }

    override suspend fun switchAnonymousToPassword(email: String, password: String): AuthResult {
        return withContext(dispatcher) {
            linkAnonymousUser(EmailAuthProvider.getCredential(email, password))
        }
    }

    override suspend fun switchAnonymousToGoogle(idToken: String): AuthResult {
        return withContext(dispatcher) {
            linkAnonymousUser(GoogleAuthProvider.getCredential(idToken, null))
        }
    }

    override suspend fun loginPasswordUser(email: String, password: String): AuthResult {
        return withContext(dispatcher) {
            auth.signInWithEmailAndPassword(email, password).await()?.let { authResult ->
                authResult.user?.let { user ->
                    AuthResult.User(id = user.uid, email = user.email ?: "", name = user.displayName ?: "", provider = authResult.credential.provider)
                }
            } ?: AuthResult.Failure("Login failed")
        }
    }

    override suspend fun loginGoogle(email: String, password: String): AuthResult {
        TODO("Not yet implemented")
    }

    private suspend fun linkAnonymousUser(credential: AuthCredential): AuthResult {
        return auth.currentUser?.linkWithCredential(credential)?.await()?.user?.let { user ->
            AuthResult.User(id = user.uid, email = user.email ?: "", name = user.displayName ?: "", provider = credential.provider)
        } ?: AuthResult.Failure("Failed to link anonymous user")
    }
}

sealed interface AuthResult {
    class User(val id: String, val email: String, val name: String, val provider: String) : AuthResult
    class Failure(error: String?) : AuthResult
}