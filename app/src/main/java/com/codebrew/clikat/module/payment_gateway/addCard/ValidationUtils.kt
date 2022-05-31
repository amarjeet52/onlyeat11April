package com.codebrew.clikat.module.payment_gateway.addCard

import androidx.core.util.PatternsCompat
import java.util.regex.Pattern

object ValidationUtils {
    // Should contain at least 1 lower case, 1 upper case and 1 digit
    private val PASSWORD_PATTERN by lazy { Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$") }

    //Minimum eight and maximum 10 characters, at least one uppercase letter, one lowercase letter, one number and one special character
    //private val PASSWORD_PATTERN1 by lazy { Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\$@\$!%*?&])[A-Za-z\\d\$@\$!%*?&]{8,64}") }
    private val PASSWORD_PATTERN1 by lazy { Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&.])[A-Za-z\\d$@$!%*?&.]{8,}") }

    // Alphanumeric, Underscore, Dot and Hyphen only
    private val USERNAME_PATTERN by lazy { Pattern.compile("^[ \\w_.-]*$") }

    private val PHONE_ALL_ZEROS by lazy { Pattern.compile("^[0]+$") }
    private val PHONE_ALL_ZEROS_format by lazy { Pattern.compile("^[0]+$") }
    private val EmailValidation by lazy { Pattern.compile("[a-zA-Z0-9_\\-\\&\\*\\+='/\\{\\}~][a-zA-Z0-9_\\-\\.&\\*\\+='/\\{\\}~]* ") }

    fun isUsernameLengthValid(username: String) = username.length >= 6

    fun isUsernameCharactersValid(username: String) = USERNAME_PATTERN.matcher(username).matches()

    fun isEmailValid(email: String) = PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()

    fun isEmailBeginwithDot(email: String) = EmailValidation.matcher(email).matches()
    fun isPhoneNumberAllZeros(username: String) = PHONE_ALL_ZEROS.matcher(username).matches()
    fun isPhoneNumberFormatAllZeros(phone: String) = PHONE_ALL_ZEROS_format.matcher(phone).matches()

    fun isPasswordLengthValid(password: String) = password.length in 6..64

    fun isPasswordCharactersValid(password: String) = PASSWORD_PATTERN1.matcher(password).matches()

    fun isPhoneNumberValid(phoneNumber: String) = phoneNumber.length in 6..15

    fun isPasscodeValid(phoneNumber: String) = phoneNumber.length in 4..6

    fun checkName(name: String): Boolean {
        return name.matches("^[a-zA-Z\\s]+$".toRegex())
    }

    fun checkPostalCode(name: String): Boolean {
        return name.matches("^[0-9a-zA-Z\\s]+$".toRegex())
    }

    fun checkPhoneNumber(phone: String): Boolean {
        return phone.matches("^[0-9\\s]+$".toRegex())
    }

    fun isPostalCodeLengthValid(password: String) = password.length in 3..10


}