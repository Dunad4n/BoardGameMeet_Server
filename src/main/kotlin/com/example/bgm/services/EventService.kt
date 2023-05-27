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
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class EventService {

    @Autowired
    lateinit var eventRepo: EventRepo

    @Autowired
    lateinit var personRepo: PersonRepo

    @Autowired
    lateinit var itemRepo: ItemRepo

    @Autowired
    lateinit var roleRepo: RoleRepo

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

    private fun mapToEventResponseEntity(event: Event, items: List<ItemResponseEntity>, person: Person): EventResponseEntity {
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
                                   items,
                                   event.host == person)
    }

    private fun mapToItemResponseEntity(item: Item): ItemResponseEntity {
        return ItemResponseEntity(item.name,
                                  item.marked)
    }

    fun getEvent(id: Long, authPerson: JwtPerson): EventResponseEntity {
        val event = eventRepo.findById(id).get()
        val person = personRepo.findByNickname(authPerson.username)
            ?: throw Exception("person with nickname ${authPerson.username} does not exist")
        val items = event.items
        return mapToEventResponseEntity(event, items.map { mapToItemResponseEntity(it) }, person)
    }

    fun createEvent(createEventRequest: CreateEventRequestEntity, hostId: Long?) {
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
        eventRepo.save(event)
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
                          pageable: Pageable,
                          authPerson: JwtPerson?): List<MainPageEventResponseEntity> {
        var events: Page<Event>? = null
        if (authPerson == null){
            events = if (search != null) {
                eventRepo.findAllByCityAndNameContainingAndDateAfter(city, search, pageable)
            } else {
                eventRepo.findAllByCityAndDateAfter(city, pageable)
            }
        }
        else{
            val person = personRepo.findByNickname(authPerson.username)
            if (person != null) {
                events = if(person.age != null) {
                    if (search != null) {
                        eventRepo.findAllByCityAndNameContainingAndMinAgeLessThanEqualAndMaxAgeGreaterThanEqualAndMembersNotContainingAndDateAfter(city, search, person.age!!, person.age!!, pageable, person)
                    } else {
                        eventRepo.findAllByCityAndMinAgeLessThanEqualAndMaxAgeGreaterThanEqualAndMembersNotContainingAndDateAfter(city, person.age!!, person.age!!, pageable, person)
                    }
                } else{
                    if (search != null) {
                        eventRepo.findAllByCityAndNameContainingAndDateAfter(city, search, pageable)
                    } else {
                        eventRepo.findAllByCityAndDateAfter(city, pageable)
                    }
                }
            }
        }
        if (events != null)
            return sortEventsForMainPage(events).map { mapToMainPageEventsResponseEntity(it) }
        return arrayListOf()
    }

    private fun sortEventsForMainPage(events: Page<Event>): List<Event> {
        return events.sortedWith(compareBy({ it.date.dayOfYear }, { it.date.year }, { it.membersForFull() }))
    }

    fun getMyEventsPageEvent(authPerson: JwtPerson, pageable: Pageable): ArrayList<MyEventsResponseEntity> {
        val person = personRepo.findByNickname(authPerson.username)
            ?: throw Exception("person with nickname ${authPerson.username} does not exist")
        val myEvents = eventRepo.findAllByMembersContains(person, pageable)
        val res = arrayListOf<MyEventsResponseEntity>()
        if (myEvents != null)
            for (event in sortEventsForMyEventPage(myEvents)) {
                res.add(mapToMyEventsResponseEntity(event, person))
            }
        return res
    }

    fun banPerson(eventId: Long, userNickname: String, authPerson: JwtPerson) {
        val host = personRepo.findByNickname(authPerson.username)
        val event = eventRepo.findById(eventId).get()
        if (host != event.host) {
            throw Exception("only host can ban person")
        }
        val user = personRepo.findByNickname(userNickname)
        if (user != null) {
            if (user == host) {
                throw Exception("host can not be banned")
            }
            event.ban(user)
        }
        eventRepo.save(event);
    }

    fun getItems(id: Long, pageable: Pageable): List<ItemResponseEntity> {
//        val items = eventRepo.findById(id).get().items
        val items = itemRepo.findAllByEvent(eventRepo.findById(id).get(), pageable)
        return items.toList().map { mapToItemResponseEntity(it) }
    }

    fun editItems(id: Long, editItemsRequest: List<EditItemsRequestEntity>, hostId: Long?) {
        val event = eventRepo.findById(id).get()
        if(hostId != event.host.id) {
            throw Exception("only host can edit items")
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

    private fun sortEventsForMyEventPage(events: Page<Event>): List<Event> {
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
}