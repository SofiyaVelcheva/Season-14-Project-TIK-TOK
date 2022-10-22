package com.tiktok.model.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "subscribers")
public class Subscriber {
    @EmbeddedId
    private SubscriberKey id;

    @ManyToOne
    @MapsId("publisherId")
    @JoinColumn(name = "publisher_id")
    private User pubUser;

    @ManyToOne
    @MapsId("subscriberId")
    @JoinColumn(name = "subscriber_id")
    private User subUser;




}
