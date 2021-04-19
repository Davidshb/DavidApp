package com.eseo.a2.davidapp.data.location

import kotlinx.serialization.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * la class stocké dans mes préférences
 * @param latitude latitude de la localisation
 * @param longitude longitude de la localisation
 * @param date Date à laquelle la localisation a été effectuée
 * @param address Addresse troucé gràce à geocoder s'il elle existe
 */
@ExperimentalSerializationApi
@kotlinx.serialization.Serializable
data class MyLocation(
    val latitude: Double,
    val longitude: Double,
    @Serializable(with = DateSerializer::class)
    val date: Date,
    var address: String? = null
)

@ExperimentalSerializationApi
@Serializer(forClass = Date::class)
object DateSerializer : KSerializer<Date> {
    private val df: DateFormat by lazy {
        SimpleDateFormat(
            "dd/MM/yyyy HH:mm:ss.SSS",
            Locale.getDefault()
        )
    }

    override fun serialize(encoder: Encoder, value: Date) {
        encoder.encodeString(df.format(value))
    }

    override fun deserialize(decoder: Decoder): Date {
        var res = df.parse(decoder.decodeString())
        if (res == null)
            res = Date()
        return res
    }
}
