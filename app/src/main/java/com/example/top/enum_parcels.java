package com.example.top;

public enum enum_parcels {
    uid(1),
    owner_uid(1),
    owner_room_name(1),
    owner_ryosei_name(1),
    register_datetime(1),
    register_staff_uid(1),
    register_staff_room_name(1),
    register_staff_ryosei_name(1),
    placement(100),
    fragile(100),
    is_released(100),
    release_agent_uid(1),
    release_datetime(1),
    release_staff_uid(1),
    release_staff_room_name(1),
    release_staff_ryosei_name(1),
    checked_count(100),
    is_lost(100),
    is_returned(100),
    returned_datetime(1),
    is_operation_error(100),
    operation_error_type(100),
    note(1),
    is_deleted(100),
    sharing_status(100);
    private final int code;
    enum_parcels(int i) {
        this.code=i;
    }
    int getCode(){
        return code;
    }
}
