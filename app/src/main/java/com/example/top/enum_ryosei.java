package com.example.top;

public enum enum_ryosei {
    uid(1),
    room_name(1),
    ryosei_name(1),
    ryosei_name_kana(1),
    ryosei_name_alphabet(1),
    block_id(100),
    slack_id(1),
    status(100),
    parcels_current_count(100),
    parcels_total_count(100),
    parcels_total_waittime(1),
    last_event_id(1),
    last_event_datetime(1),
    created_at(1),
    updated_at(1),
    sharing_status(100),
    sharing_time(1);

    private final int code;
    enum_ryosei(int i) {
        this.code=i;
    }
    int getCode(){
        return code;
    }
}
