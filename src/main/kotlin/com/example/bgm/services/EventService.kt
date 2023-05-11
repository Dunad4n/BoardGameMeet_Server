package com.example.bgm.services

import com.example.bgm.controller.dto.*
import com.example.bgm.entities.Event
import com.example.bgm.entities.Item
import com.example.bgm.entities.Person
import com.example.bgm.jwt.JwtPerson
import com.example.bgm.repositories.EventRepo
import com.example.bgm.repositories.ItemRepo
import com.example.bgm.repositories.PersonRepo
import com.example.bgm.repositories.RoleRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class EventService(

    @Autowired
    private var eventRepo: EventRepo,

    @Autowired
    private var personRepo: PersonRepo,

    @Autowired
    private var itemRepo: ItemRepo,

    @Autowired
    private var roleRepo: RoleRepo

) {

    private fun mapToMainPageEventsResponseEntity(event: Event): MainPageEventResponseEntity {
        return MainPageEventResponseEntity(event.id,
                                           event.name,
                                           event.game,
                                           event.address,
                                           event.date,
                                           event.members.size,
                                           event.maxPersonCount,
                                           event.minAge,
                                           event.maxAge)
    }

    private fun mapToMyEventsResponseEntity(event: Event, user: Person): MyEventsResponseEntity {
        return MyEventsResponseEntity(event.id,
                                      event.name,
                                      event.game,
                                      event.address,
                                      event.date,
                                      event.members.size,
                                      event.maxPersonCount,
                                      event.minAge,
                                      event.maxAge,
                                 event.host == user)
    }

    private fun mapToCreateEventResponseEntity(event: Event): CreateEventResponseEntity {
        return CreateEventResponseEntity(event.id,
                                         event.name,
                                         event.game,
                                         event.city,
                                         event.address,
                                         event.date,
                                         event.members.size,
                                         event.maxPersonCount,
                                         event.minAge,
                                         event.maxAge,
                                         event.description,
                                         event.host.id)
    }

    private fun mapToEventResponseEntity(event: Event, items: List<ItemResponseEntity>): EventResponseEntity {
        return EventResponseEntity(event.id,
                                   event.name,
                                   event.game,
                                   event.address,
                                   event.date,
                                   event.members.size,
                                   event.maxPersonCount,
                                   event.minAge,
                                   event.maxAge,
                                   event.description,
                                   items)
    }

    private fun mapToItemResponseEntity(item: Item): ItemResponseEntity {
        return ItemResponseEntity(item.name,
                                  item.marked)
    }

    fun getEvent(id: Long?): EventResponseEntity {
        if (id == null) {
            throw Exception("id is null")
        }
        val event = eventRepo.findById(id).get()
        val items = event.items
        return mapToEventResponseEntity(event, items.map { mapToItemResponseEntity(it) })
    }

    fun createEvent(createEventRequest: CreateEventRequestEntity, hostId: Long?): CreateEventResponseEntity {
        if (hostId == null) {
            throw Exception("person id is null")
        }
        val host = personRepo.findById(hostId).get()
        val event = Event(createEventRequest.name,
                          createEventRequest.game,
                          createEventRequest.city,
                          createEventRequest.address,
                          createEventRequest.date,
                          createEventRequest.maxPersonCount,
                          host,
                          createEventRequest.minAge,
                          createEventRequest.maxAge,
                          createEventRequest.description)
        event.members.add(host)
        return mapToCreateEventResponseEntity(eventRepo.save(event))
    }

    fun updateEvent(updateRequest: UpdateEventRequest) {
        val event = eventRepo.findById(updateRequest.id).get()
        event.name = updateRequest.name
        event.game = updateRequest.game
        event.city = updateRequest.city
        event.address = updateRequest.address
        event.date = LocalDateTime.now()
        event.maxPersonCount = updateRequest.maxPersonCount
        event.maxAge = updateRequest.maxAge
        event.minAge = updateRequest.minAge
        event.description = updateRequest.name
        eventRepo.save(event)
    }

    fun deleteEvent(id: Long, authPerson: JwtPerson) {
        if (authPerson.id != eventRepo.findById(id).get().host.id &&
            personRepo.findByNickname(authPerson.username)?.roles?.
            contains(roleRepo.findByName("ROLE_ADMIN")) == false) {
            throw Exception("this person can not delete event with id $id")
        }
        eventRepo.deleteById(id)
    }

    fun getMainPageEvents(city: String,
                          search: String?,
                          authPerson: JwtPerson?): List<MainPageEventResponseEntity> {
        var events = if (search != null) {
            eventRepo.findAllByCityAndName(city, search)
        } else {
            eventRepo.findAllByCity(city)
        }
        if (events == null) {
            return arrayListOf()
        }
        events = sortEventsForMainPage(filterEventsForActiveStatus(events))
        events = if (authPerson != null) {
            val person = personRepo.findByNickname(authPerson.username)
            if (person?.age != null) {
                filterEventsForAge(events, person)
            } else {
                filterEventsForNullAge(events)
            }
        } else {
            filterEventsForNullAge(events)
        }
        return events.map { mapToMainPageEventsResponseEntity(it) }
    }

//    // поменять events на запрос к бд
//    fun getEventsWithSearch(user: Int, start: Int, search: String): ArrayList<EventsResponseEntity> {
//        val res: ArrayList<EventsResponseEntity> = arrayListOf()
//        for(event in events)
//            res.add(mapToEventsResponseEntity(event))
//        return res
//    }

    fun getMyEventsPageEvent(authPerson: JwtPerson): ArrayList<MyEventsResponseEntity> {
        val person = personRepo.findByNickname(authPerson.username)
            ?: throw Exception("person with nickname ${authPerson.username} does not exist")
        val myEvents = person.events
        val res = arrayListOf<MyEventsResponseEntity>()
        for (event in sortEventsForMyEventPage(myEvents)) {
            res.add(mapToMyEventsResponseEntity(event, person))
        }
        return res
    }

    fun banPerson(eventId: Long, userNickname: String) {
        val user = personRepo.findByNickname(userNickname)
        if (user != null) {
            eventRepo.findById(eventId).get().ban(user)
        }
    }

    fun getItems(id: Long): List<ItemResponseEntity> {
        val items = eventRepo.findById(id).get().items
        return items.map { mapToItemResponseEntity(it) }
    }

    fun editItems(id: Long, editItemsRequest: List<EditItemsRequestEntity>, hostId: Long?) {
        val event = eventRepo.findById(id).get()
        if(hostId != event.host.id) {
            throw Exception("this person can not edit chosen event")
        }
        if(editItemsRequest.size < event.items.size) {
            for (i in editItemsRequest.size until event.items.size) {
                itemRepo.delete(event.items[i])
            }
        }
//        else if (editItemsRequest.size > event.items.size) {
//            for (i in event.items.size until editItemsRequest.size) {
//                event.items.add(Item(null, null))
//            }
//        }
        event.editItems(editItemsRequest)
        for (item in event.items) {
            itemRepo.save(item)
        }
        eventRepo.save(event)
    }

    fun markItems(eventId: Long, markItemsRequest: MarkItemsRequestEntity) {
        val event = eventRepo.findById(eventId).get()
        if(markItemsRequest.markedStatuses.size < event.items.size) {
            throw Exception("incorrect size of marked statuses")
        }
        for (i in event.items.indices){
            event.items[i].marked = markItemsRequest.markedStatuses[i]
        }
    }

    private fun sortEventsForMainPage(events: List<Event>): List<Event> {
        return events.sortedWith(compareBy({ it.date.year }, { it.date.dayOfYear }, { it.membersForFull() }))
    }

    private fun sortEventsForMyEventPage(events: List<Event>): List<Event> {
        val activeEvents = mutableListOf<Event>()
        val inactiveEvents = mutableListOf<Event>()
        val resultEvents = mutableListOf<Event>()
        for (event in events) {
            if (event.isActive()) {
                activeEvents.add(event)
            } else {
                inactiveEvents.add(event)
            }
        }
        resultEvents.addAll(activeEvents.sortedWith(compareBy { it.date.toEpochSecond(ZoneOffset.UTC) }))
        resultEvents.addAll(inactiveEvents.sortedWith(compareBy { -it.date.toEpochSecond(ZoneOffset.UTC) }))
        return resultEvents
    }

    private fun filterEventsForActiveStatus(events: List<Event>): List<Event> {
        return events.filter { it.isActive() }
    }

    /** Если у пользователя указан возраст **/
    private fun filterEventsForAge(events: List<Event>, person: Person): List<Event> {
        if (person.age == null) {
            throw Exception("age of this person is null")
        }
        return events.filter { it.minAge != null && it.maxAge != null }
                     .filter { it.minAge!! <= person.age!! && it.maxAge!! >= person.age!! }
    }

    /** Если пользователь неавторизован или возраст не указан **/
    private fun filterEventsForNullAge(events: List<Event>): List<Event> {
        return events.filter { it.minAge == null && it.maxAge == null }
    }
}