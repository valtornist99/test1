package com.quipy.server.eventsourcing

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*
import kotlin.reflect.KClass

interface EventSourcingEntity {
    val entityId: String
    val createdAt: Long
    val updatedAt: Long
    var version: Int
}

interface UpdateEventName {
    val eventClass: KClass<*>
}

abstract class UpdateEvent<Entity : EventSourcingEntity, EventTitlesEnum : UpdateEventName> {
    abstract val id: String
    abstract val entityId: UUID
    abstract val correlationId: String?
    abstract val name: EventTitlesEnum
    var createdAt: Long = System.currentTimeMillis()

    abstract infix fun applyTo(entity: Entity)
}

fun <T : EventSourcingEntity, Title : UpdateEventName> T.sequentiallyApply(
    updateEvents: List<UpdateEvent<T, Title>>,
    upToEvent: UUID? = null
) {
    updateEvents.sortedBy { it.createdAt }
        .takeWhile { it.entityId != upToEvent }
        .forEach { event -> event applyTo this }

    this.version = updateEvents.size
}


@Suppress("UNCHECKED_CAST")
@Document(collection = "event_sourcing_records")
abstract class EventSourcingRecord<E : EventSourcingEntity, EventName : UpdateEventName>(
    @Id
    var id: String = UUID.randomUUID().toString(),
) {
    abstract val correlationId: String?
    abstract val entityId: String
    abstract val eventTitle: EventName
    abstract val payload: String

    fun ObjectMapper.getUpdateEvent(): UpdateEvent<E, EventName> {
        val eventSourcingRecord = this@EventSourcingRecord
        return this.readValue(
            eventSourcingRecord.payload,
            eventSourcingRecord.eventTitle.eventClass.java
        ) as UpdateEvent<E, EventName>
    }
}

/**
 * todo comments everywhere
 */
inline fun <reified E : EventSourcingEntity, EventName : UpdateEventName, EventTitlesEnum : UpdateEventName>
    List<EventSourcingRecord<E, EventName>>.turnToEntity(
    entityId: String,
    upToEvent: UUID? = null,
    mapRecordToEvent: EventSourcingRecord<E, EventName>.() -> UpdateEvent<E, EventTitlesEnum>
): E {
    val defaultConstructor = try {
        E::class.java.getConstructor(java.lang.String::class.java)
    } catch (e: NoSuchMethodException) {
        throw IllegalStateException(
            "There is no suitable constructor for event sourcing entity: ${E::class.java.simpleName}",
            e
        )
    }

    val eventSouringEntity =
        defaultConstructor.newInstance(entityId) // todo sukhoa actually we even don't need the ID to be passed

    this.map { it.mapRecordToEvent() }.toList().also { updateEvents ->
        eventSouringEntity.sequentiallyApply(
            updateEvents = updateEvents,
            upToEvent = upToEvent
        )
    }

    return eventSouringEntity
}


