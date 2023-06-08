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
        if(request.name.length > 30) {
            message = "the name must be shorter than 30 characters"
            return false
        }
        if(request.game.length > 30) {
            message = "the game must be shorter than 30 characters"
            return false
        }
        if(request.address.length > 100) {
            message = "the address must be shorter than 100 characters"
            return false
        }
        if(request.city.length > 30) {
            message = "the city must be shorter than 30 characters"
            return false
        }
        if(request.description.length > 30) {
            message = "the description must be shorter than 30 characters"
            return false
        }
        // можно добавить времени чтоб не создать мероприятие через минуту
        if(request.date.isBefore(LocalDateTime.now())) {
            message = "Дата не может быть раньше текущей"
            return false
        }
        if (request.minAge != null && request.maxAge != null) {
            if (request.minAge!! < 0 || request.minAge!! > 100) {
                message = "Минимальный возраст не может быть меньше 0, и больше 100"
                return false
            }
            if (request.maxAge!! < 0 || request.maxAge!! > 100) {
                message = "Минимальный возраст не может быть меньше 0, и больше 100"
                return false
            }
            if (request.minAge!! > request.maxAge!!) {
                message = "Минимальный возраст не может быть больше максимального"
                return false
            }
        }
        if(request.maxPersonCount < 2) {
            message = "Максимально количество участников не может быть меньше 2"
            return false
        }
        return true
    }

    fun validate(request: UpdateEventRequest): Boolean{
        //можно уменьшить количесто для имен
        if(request.name.length > 30) {
            message = "the name must be shorter than 30 characters"
            return false
        }
        if(request.game.length > 30) {
            message = "the game must be shorter than 30 characters"
            return false
        }
        if(request.address.length > 100) {
            message = "the address must be shorter than 100 characters"
            return false
        }
        if(request.city.length > 30) {
            message = "the city must be shorter than 30 characters"
            return false
        }
        if(request.description.length > 300) {
            message = "the description must be shorter than 300 characters"
            return false
        }
        // можно добавить времени чтоб не создать мероприятие через минуту
        if(request.date.isBefore(LocalDateTime.now())) {
            message = "Дата не может быть раньше текущей"
            return false
        }
        // можно добавить проверки на max значение
        if (request.minAge != null && request.maxAge != null) {
            if (request.minAge < 0 || request.minAge > 100) {
                message = "Минимальный возраст не может быть меньше 0, и больше 100"
                return false
            }
            if (request.maxAge < 0 || request.maxAge > 100) {
                message = "Минимальный возраст не может быть меньше 0, и больше 100"
                return false
            }
            if (request.minAge > request.maxAge) {
                message = "Минимальный возраст не может быть больше максимального"
                return false
            }
        }
        if(request.maxPersonCount < 2) {
            message = "Максимально количество участников не может быть меньше 2"
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
            message = "the name must be shorter than 30 characters"
            return false
        }
        if(request.nickname.length > 30) {
            message = "the nickname must be shorter than 30 characters"
            return false
        }
        if(request.city.length > 30) {
            message = "the city must be shorter than 30 characters"
            return false
        }
        // можно добавить проверки на max значение
        if(request.age != null) {
            if (request.age < 0 || request.age > 100) {
                message = "Возраст не может быть меньше 0 или больше 100"
                return false
            }
        }
        return true
    }

    fun validate(request: CreatePersonRequestEntity):Boolean{
        if(request.name.length > 30) {
            message = "the name must be shorter than 30 characters"
            return false
        }
        if(request.nickname.length > 30) {
            message = "the nickname must be shorter than 30 characters"
            return false
        }
        if(request.city.length > 30) {
            message = "the city must be shorter than 30 characters"
            return false
        }
        if(request.secretWord.length > 30) {
            message = "the secretWord must be shorter than 30 characters"
            return false
        }
        if(request.password.length > 30) {
            message = "the password must be shorter than 30 characters"
            return false
        }
        if(request.age != null) {
            if (request.age < 0 || request.age > 100) {
                message = "Возраст не может быть меньше 0 или больше 100"
                return false
            }
        }
        return true
    }
}