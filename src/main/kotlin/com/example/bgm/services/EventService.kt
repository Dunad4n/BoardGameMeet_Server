package com.example.bgm.services

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
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneOffset
import com.example.bgm.controller.dto.MainPageEventResponseEntity
import com.example.bgm.controller.dto.MyEventsResponseEntity
import com.example.bgm.controller.dto.EventResponseEntity
import com.example.bgm.controller.dto.CreateEventResponseEntity
import com.example.bgm.controller.dto.ItemResponseEntity
import com.example.bgm.controller.dto.EditItemsRequestEntity
import com.example.bgm.controller.dto.MarkItemRequestEntity
import com.example.bgm.controller.dto.UpdateEventRequest
import com.example.bgm.controller.dto.CreateEventRequestEntity
import com.example.bgm.entities.enums.Gender
import org.springframework.data.domain.PageImpl
import java.lang.Math.min
import java.sql.Date
import java.time.LocalDate


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
                                           event.maxAge,
                                           event.description)
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
                                      event.description,
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

    private fun mapToItemResponseEntity(item: Item): ItemResponseEntity {
        return ItemResponseEntity(item.id,
                                  item.name,
                                  item.marked)
    }

    private fun mapToItems(editItemsRequest: List<EditItemsRequestEntity>, event: Event): MutableList<Item> {
        var items = mutableListOf<Item>()
        for (item in editItemsRequest) {
            val newItem = Item(item.name, item.marked)
            newItem.event = event
            items.add(newItem)
        }
        return items
    }


    fun getEvent(id: Long?, authPerson: JwtPerson): EventResponseEntity {
        if (id == null) {
            throw Exception("event id is null")
        }
        val event = eventRepo.findById(id).get()
        val person = personRepo.findByNickname(authPerson.username)
            ?: throw Exception("person with nickname ${authPerson.username} does not exist")
        val items = event.items
        return mapToEventResponseEntity(event, items.map { mapToItemResponseEntity(it) }, person)
    }

    fun createEvent(createEventRequest: CreateEventRequestEntity, hostId: Long?): ResponseEntity<*> {
        if (hostId == null) {
            throw Exception("person id is null")
        }
//        if (createEventRequest.minAge!! > createEventRequest.maxAge!!) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body("Минимальный возраст не может быть больше максимального")
//        }
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
        return ResponseEntity.ok("done")
    }

    fun updateEvent(updateRequest: UpdateEventRequest, authPerson: JwtPerson): ResponseEntity<*> {
        val event = eventRepo.findEventById(updateRequest.id).get()
        if (personRepo.findByNickname(authPerson.username) != event.host) {
            throw Exception("only host can edit event")
        }
        if (updateRequest.minAge!! > updateRequest.maxAge!!) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Минимальный возраст не может быть больше максимального")
        }
        event.name = updateRequest.name
        event.game = updateRequest.game
        event.city = updateRequest.city
        event.address = updateRequest.address
        event.date = updateRequest.date
        event.maxPersonCount = updateRequest.maxPersonCount
        event.maxAge = updateRequest.maxAge
        event.minAge = updateRequest.minAge
        event.description = updateRequest.description
        eventRepo.save(event)
        return ResponseEntity.ok("done")
    }

    fun deleteEvent(id: Long?, authPerson: JwtPerson) {
        if (id == null) {
            throw Exception("event id is null")
        }
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
                        eventRepo.findAllByAgeAndName(city, search, person.age!!, pageable, person.events)
                    } else {
                        eventRepo.findAllByAge(city, person.age, pageable, person.events)
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
        val myEvents = findMyEvents(person.events, pageable)
        val res = arrayListOf<MyEventsResponseEntity>()
        for (event in sortEventsForMyEventPage(myEvents)) {
            res.add(mapToMyEventsResponseEntity(event, person))
        }
        return res
    }

    fun findMyEvents(events: List<Event>, pageable: Pageable): Page<Event> {
        val pastEvents = eventRepo.findPastEvents(events)
        val futureEvents = eventRepo.findFutureEvents(events)
        val allEvents = mutableListOf<Event>()
        allEvents.addAll(pastEvents.sortedWith(compareBy { it.date.toEpochSecond(ZoneOffset.UTC) }))
        allEvents.addAll(futureEvents.sortedWith(compareBy { -it.date.toEpochSecond(ZoneOffset.UTC) }))
//        val sortedEvents = allEvents.sortedWith(compareBy { it.date.toEpochSecond(ZoneOffset.UTC) })
        val start = pageable.offset.toInt()
        val end = min((start + pageable.pageSize), allEvents.size)
        return PageImpl(allEvents.subList(start, end), pageable, allEvents.size.toLong())
    }

    fun banPerson(eventId: Long?, userNickname: String, authPerson: JwtPerson) {
        if (eventId == null) {
            throw Exception("event id is null")
        }
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

    fun getItems(id: Long?, authPerson: JwtPerson): List<ItemResponseEntity> {
        if (id == null) {
            throw Exception("event id is null")
        }
        val event = eventRepo.findById(id).get()
        val person = personRepo.findByNickname(authPerson.username)
        if (!event.members.contains(person)) {
            throw Exception("only members can get items")
        }
//        val items = eventRepo.findById(id).get().items
        val items = itemRepo.findAllByEvent(eventRepo.findById(id).get())
        return items.toList().map { mapToItemResponseEntity(it) }
    }

    @Transactional
    fun deleteItems(eventId: Long?, authPerson: JwtPerson) {
        if (eventId == null) {
            throw Exception("event id is null")
        }
        val event = eventRepo.findEventById(eventId).get()
        if (event.host.id != authPerson.id) {
            throw Exception("only host can delete items")
        }
        itemRepo.deleteAllByEvent(event)
        itemRepo.flush()
    }

    fun editItems(eventId: Long?, editItemsRequest: List<EditItemsRequestEntity>, hostId: Long?) {
        if (eventId == null) {
            throw Exception("event id is null")
        }
        val event = eventRepo.findById(eventId).get()
        if(hostId != event.host.id) {
            throw Exception("only host can edit items")
        }
        for (item in event.items) {
            itemRepo.delete(item)
        }
        itemRepo.flush()
        for(item in mapToItems(editItemsRequest, event)) {
            itemRepo.save(item)
        }
    }

    fun markItem(eventId: Long?, markItemRequest: MarkItemRequestEntity, authPerson: JwtPerson) {
        if (eventId == null) {
            throw Exception("event id is null")
        }
        val person = personRepo.findByNickname(authPerson.username)
        val event = eventRepo.findById(eventId).get()
        if (!event.members.contains(person)) {
            throw Exception("only member can mark items")
        }
        if (markItemRequest.itemId == null) {
            throw Exception("item id is null")
        }
        val item = itemRepo.findById(markItemRequest.itemId).get()
        item.marked = markItemRequest.markedStatus;
        itemRepo.save(item)
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