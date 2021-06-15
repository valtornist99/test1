package com.quipy.server.query

import org.springframework.data.mongodb.core.FindAndReplaceOptions
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

interface VersionedEntity {
    val id: String
    val version: Int
}

inline fun <reified E : VersionedEntity> MongoTemplate.replaceOlderEntityIfExists(replacement: E): E? {
    return update(E::class.java)
        .matching(Query.query(Criteria.where("_id").`is`(replacement.id).and("version").lt(replacement.version)))
        .replaceWith(replacement)
        .withOptions(FindAndReplaceOptions.options().returnNew())
        .findAndReplace()
        .orElse(null)
}

inline fun <reified E : VersionedEntity> MongoTemplate.replaceOlderEntityOrInsert(replacement: E): E? {
    return update(E::class.java)
        .matching(Query.query(Criteria.where("_id").`is`(replacement.id).and("version").lt(replacement.version)))
        .replaceWith(replacement)
        .withOptions(FindAndReplaceOptions.options().returnNew())
        .withOptions(FindAndReplaceOptions.options().upsert())
        .findAndReplace()
        .orElse(null)
}

inline fun <reified E : VersionedEntity> MongoTemplate.insertEntity(entity: E): E? {
    return try {
        insert(entity)
    } catch (th: Throwable) {
        th.printStackTrace()
        return null
    }
}

inline fun <reified E : VersionedEntity> MongoTemplate.updateWithLatestVersion(entity: E): E? {
    return if (exists(Query.query(Criteria.where("_id").`is`(entity.id)), E::class.java)) {
        replaceOlderEntityIfExists(entity)
    } else {
        replaceOlderEntityOrInsert(entity)
    }
}

