package com.rposcro.jwavez.serial.enums;

import com.rposcro.jwavez.core.utils.EncodableConstant;
import com.rposcro.jwavez.core.utils.EncodableConstantsRegistry;

public enum SerialCommand implements EncodableConstant {

    GET_INIT_DATA(0x02),
    APPLICATION_NODE_INFO(0x03),
    APPLICATION_COMMAND_HANDLER(0x04),
    GET_CONTROLLER_CAPABILITIES(0x05),
    SET_TIMEOUTS(0x06),
    GET_CAPABILITIES(0x07),
    SOFT_RESET(0x08),
    SERIAL_API_SETUP(0x0b),
    RF_RECEIVE_MODE(0x10),
    SET_SLEEP_MODE(0x11),
    SEND_NODE_INFO(0x12),
    SEND_DATA(0x13),
    SEND_DATA_MULTI(0x14),
    GET_VERSION(0x15),
    SEND_DATA_ABORT(0x16),
    RF_POWER_LEVEL_SET(0x17),
    SEND_DATA_META(0x18),
    GET_RANDOM(0x1c),
    MEMORY_GET_ID(0x20),
    MEMORY_GET_BYTE(0x21),
    MEMORY_PUT_BYTE(0x22),
    READ_MEMORY(0x23),
    WRITE_MEMORY(0x24),
    NVM_GET_ID(0x29),
    NVM_EXT_READ_LONG_BUFFER(0x2a),
    NVM_EXT_WRITE_LONG_BUFFER(0x2b),
    NVM_EXT_READ_LONG_BYTE(0x2c),
    NVM_EXT_WRITE_LONG_BYTE(0x2d),
    GET_NETWORK_STATS(0x3a),
    GET_BACKGROUND_RSSI(0x3b),
    SET_LEARN_NODE_STATE(0x40),
    IDENTIFY_NODE(0x41),
    SET_DEFAULT(0x42),
    NEW_CONTROLLER(0x43),
    REPLICATION_COMMAND_COMPLETE(0x44),
    REPLICATION_SEND_DATA(0x45),
    ASSIGN_RETURN_ROUTE(0x46),
    DELETE_RETURN_ROUTE(0x47),
    REQUEST_NODE_NEIGHBOUR_UPDATE(0x48),
    APPLICATION_UPDATE(0x49),
    ADD_NODE_TO_NETWORK(0x4a),
    REMOVE_NODE_FROM_NETWORK(0x4b),
    CREATE_NEW_PRIMARY(0x4c),
    CONTROLLER_CHANGE(0x4d),
    SET_LEARN_MODE(0x50),
    ASSIGN_SUC_RETURN_ROUTE(0x51),
    ENABLE_SUC(0x52),
    REQUEST_NETWORK_UPDATE(0x53),
    SET_SUC_NODE_ID(0x54),
    DELETE_SUC_RETURN_ROUTE(0x55),
    GET_SUC_NODE_ID(0x56),
    SEND_SUC_ID(0x57),
    REQUEST_NODE_NEIGHBOUR_UPDATE_OPTIONS(0x5a),
    EXPLORE_REQUEST_INCLUSION(0x5e),
    REQUEST_NODE_INFO(0x60),
    REMOVE_FAILED_NODE_ID(0x61),
    IS_FAILED_NODE_ID(0x62),
    REPLACE_FAILED_NODE(0x63),
    GET_ROUTING_INFO(0x80),
    LOCK_ROUTE(0x90),
    GET_PRIORITY_ROUTE(0x92),
    SET_PRIORITY_ROUTE(0x93),
    GET_SECURITY_KEYS(0x9c),
    SLAVE_NODE_INFO(0xa0),
    APPLICATION_SLAVE_COMMAND_HANDLER(0xa1),
    SEND_SLAVE_NODE_INFO(0xa2),
    SEND_SLAVE_DATA(0xa3),
    SET_SLAVE_LEARN_MODE(0xa4),
    GET_VIRTUAL_NODES(0xa5),
    IS_VIRTUAL_NODE(0xa6),
    SET_WUT_TIMEOUT(0xb4),
    WATCH_DOG_ENABLE(0xb6),
    WATCH_DOG_DISABLE(0xb7),
    WATCH_DOG_KICK(0xb8),
    SET_EXT_INT_LEVEL(0xb9),
    RF_POWER_LEVEL_GET(0xba),
    GET_LIBRARY_TYPE(0xbd),
    SEND_TEST_FRAME(0xbe),
    GET_PROTOCOL_STATUS(0xbf),
    SET_PROMISCUOUS_MODE(0xd0),
    SET_ROUTING_MAX(0xd4),
    PROMISCUOUS_APPLICATION_COMMAND_HANDLER(0xD1),
    ZSTICK_SET_CONFIG(0xf2),
    ZSTICK_GET_CONFIG(0xf3),
    ;

    SerialCommand(int functionId) {
        EncodableConstantsRegistry.registerConstant(this, (byte) functionId);
    }

    public static SerialCommand ofCode(byte code) {
        return EncodableConstantsRegistry.constantOfCode(SerialCommand.class, code);
    }
}
