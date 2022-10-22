package com.tiktok.model.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class SubscriberKey implements Serializable {

    @Column(name = "publisher_id")
    private int publisherId;
    @Column(name = "subscriber_id")
    private int subscriberId;
}
