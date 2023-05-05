package com.example.bgm.services

import com.example.bgm.controller.*
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
        // можно добавить проверки на max значение
        if(request.minAge < 0) {
            message = "minAge must bo positive"
            return false
        }
        if(request.maxAge < 0) {
            message = "maxAge must bo positive"
            return false
        }
        if(request.maxPersonCount < 2) {
            message = "maxPersonCount must bo more than 1"
            return false
        }
        return true
    }

    fun validate(request: UpdateEventRequestEntity): Boolean{
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
        if(request.minAge < 0) {
            message = "minAge must bo positive"
            return false
        }
        if(request.maxAge < 0) {
            message = "maxAge must bo positive"
            return false
        }
        if(request.maxPersonCount < 2) {
            message = "maxPersonCount must bo more than 1"
            return false
        }
        return true
    }

    fun validate(request: EditItemsRequestEntity):Boolean{
        for(s in request.items){
            if(s.length > 255) {
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
        if(request.name.length > 255) {
            message = "the name must be shorter than 255 characters"
            return false
        }
        if(request.nickname.length > 255) {
            message = "the nickname must be shorter than 255 characters"
            return false
        }
        if(request.city.length > 255) {
            message = "the city must be shorter than 255 characters"
            return false
        }
        // можно добавить проверки на max значение
        if(request.age < 0) {
            message = "minAge must bo positive"
            return false
        }
        return true
    }

    fun validate(request: CreatePersonRequestEntity):Boolean{
        if(request.name.length > 255) {
            message = "the name must be shorter than 255 characters"
            return false
        }
        if(request.nickname.length > 255) {
            message = "the nickname must be shorter than 255 characters"
            return false
        }
        if(request.city.length > 255) {
            message = "the city must be shorter than 255 characters"
            return false
        }
        if(request.secretWord.length > 255) {
            message = "the secretWord must be shorter than 255 characters"
            return false
        }
        if(request.password.length > 255) {
            message = "the password must be shorter than 255 characters"
            return false
        }
        return true
    }
}