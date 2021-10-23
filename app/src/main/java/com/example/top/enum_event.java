package com.example.top;

public enum enum_event {
    _id(100),
    created_at(1),
    event_type(100),
    parcel_uid(100),
    ryosei_uid(100),
    room_name(1),
    ryosei_name(1),
    target_event_uid(100),
    note(1),
    is_finished(100),
    is_deleted(100),
    sharing_status(100);
    private final int code;
    enum_event(int i) {
        this.code=i;
    }
    int getCode(){
        return code;
    }
}
