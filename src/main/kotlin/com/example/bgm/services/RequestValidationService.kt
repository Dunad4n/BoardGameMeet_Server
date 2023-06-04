package com.example.bgm.services

import com.example.bgm.controller.dto.*
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class RequestValidationService {
    private var message: String = ""

    fun getMessage() = message

    fun validate(request: CreateEventRequestEntity): Boolean{
        //можно уменьшить количесто для имен
        if(request.name.length > 255) {
            message = "the name must be shorter than 255 characters"
            return false
        }
        if(request.game.length > 255) {
            message = "the game must be shorter than 255 characters"
            return false
        }
        if(request.address.length > 255) {
            message = "the address must be shorter than 255 characters"
            return false
        }
        if(request.city.length > 255) {
            message = "the city must be shorter than 255 characters"
            return false
        }
        if(request.description.length > 255) {
            message = "the description must be shorter than 255 characters"
            return false
        }
        // можно добавить времени чтоб не создать мероприятие через минуту
        if(request.date.isBefore(LocalDateTime.now())) {
            message = "outdated"
            return false
        }
        if (request.minAge!= null) {
            // можно добавить проверки на max значение
            if (request.minAge!! < 0 || request.minAge!! > 100) {
                message = "minAge must be grater than 0 or less the 100"
                return false
            }
        }
        if (request.maxAge != null) {
            if (request.maxAge!! < 0 || request.maxAge!! > 100) {
                message = "maxAge must be grater than 0 or less the 100"
                return false
            }
        }
        if (request.minAge!! > request.maxAge!!) {
            message = "minAge can not be less then maxAge"
            return false
        }
        if(request.maxPersonCount < 2) {
            message = "maxPersonCount must bo more than 1"
            return false
        }
        return true
    }

    fun validate(request: UpdateEventRequest): Boolean{
        //можно уменьшить количесто для имен
        if(request.name.length > 255) {
            message = "the name must be shorter than 255 characters"
            return false
        }
        if(request.game.length > 255) {
            message = "the game must be shorter than 255 characters"
            return false
        }
        if(request.address.length > 255) {
            message = "the address must be shorter than 255 characters"
            return false
        }
        if(request.city.length > 255) {
            message = "the city must be shorter than 255 characters"
            return false
        }
        if(request.description.length > 255) {
            message = "the description must be shorter than 255 characters"
            return false
        }
        // можно добавить времени чтоб не создать мероприятие через минуту
        if(request.date.isBefore(LocalDateTime.now())) {
            message = "outdated"
            return false
        }
        // можно добавить проверки на max значение
        if (request.minAge != null && request.maxAge != null) {
            if (request.minAge < 0 || request.minAge > 100) {
                message = "minAge must be grater than 0 or less the 100"
                return false
            }
            if (request.maxAge < 0 || request.maxAge > 100) {
                message = "maxAge must be grater than 0 or less the 100"
                return false
            }
        }
        if(request.maxPersonCount < 2) {
            message = "maxPersonCount must bo more than 1"
            return false
        }
        return true
    }

    fun validate(request: List<EditItemsRequestEntity>): Boolean{
        for(item in request){
            if(item.name.length > 255) {
                message = "the item must be shorter than 255 characters"
                return false
            }
        }
        return true
    }

    fun validate(request: CreateMessageRequestEntity):Boolean{
        if(request.text.length > 255) {
            message = "the text must be shorter than 255 characters"
            return false
        }
        return true
    }

    fun validate(request: UpdatePersonRequestEntity):Boolean{
        if(request.name.length > 30) {
            message = "the name must be shorter than 255 characters"
            return false
        }
        if(request.nickname.length > 30) {
            message = "the nickname must be shorter than 255 characters"
            return false
        }
        if(request.city.length > 255) {
            message = "the city must be shorter than 255 characters"
            return false
        }
        // можно добавить проверки на max значение
        if(request.age != null) {
            if (request.age < 0 || request.age > 100) {
                message = "Возраст может быть от 0 до 100"
                return false
            }
        }
        return true
    }

    fun validate(request: CreatePersonRequestEntity):Boolean{
        if(request.name.length > 30) {
            message = "the name must be shorter than 255 characters"
            return false
        }
        if(request.nickname.length > 30) {
            message = "the nickname must be shorter than 255 characters"
            return false
        }
        if(request.city.length > 255) {
            message = "the city must be shorter than 255 characters"
            return false
        }
        if(request.secretWord.length > 30) {
            message = "the secretWord must be shorter than 255 characters"
            return false
        }
        if(request.password.length > 30) {
            message = "the password must be shorter than 255 characters"
            return false
        }
        if(request.age != null) {
            if (request.age < 0 || request.age > 100) {
                message = "Возраст может быть от 0 до 100"
                return false
            }
        }
        return true
    }
}