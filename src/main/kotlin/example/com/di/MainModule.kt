package example.com.di

import example.com.data.MessageDataSource
import example.com.data.MessageDataSourceImpl
import example.com.room.RoomController
import org.koin.dsl.module
import org.litote.kmongo.KMongo
import org.litote.kmongo.coroutine.coroutine

val mainModule = module {
    single {
        org.litote.kmongo.reactivestreams.KMongo.createClient()
            .coroutine
            .getDatabase("message_db")
    }

    single<MessageDataSource> {
        MessageDataSourceImpl(get())
    }

    single {
        RoomController(get())
    }
}