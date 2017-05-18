package com.tsuruta.meet.objects

class Event()
{
    internal lateinit var name: String
    internal lateinit var location: String
    internal lateinit var description: String
    internal lateinit var date: String
    internal lateinit var time: String
    internal lateinit var imageUrl: String

    constructor(name: String, location: String, description: String, date: String, time: String, imageUrl: String): this()
    {
        this.name = name
        this.location = location
        this.description = description
        this.date = date
        this.time = time
        this.imageUrl = imageUrl
    }
}
