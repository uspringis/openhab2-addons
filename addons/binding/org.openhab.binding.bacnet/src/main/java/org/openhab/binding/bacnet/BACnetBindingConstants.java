/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.bacnet;

import java.util.Set;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

import com.google.common.collect.ImmutableSet;

/**
 * The {@link bacnetBinding} class defines common constants, which are
 * used across the whole binding.
 *
 * @author ugis.springis@gmail.com - Initial contribution
 */
public class BACnetBindingConstants {

    public static final String BINDING_ID = "bacnet";

    // List of all Thing Type UIDs
    public final static ThingTypeUID BACNETIP_THING_TYPE = new ThingTypeUID(BINDING_ID, "bacnetip");
    public final static ThingTypeUID DEVICE_THING_TYPE = new ThingTypeUID(BINDING_ID, "device");

    // List of all Channel Type ids
    public final static String CHANNEL_TYPE_ANALOG_INPUT = "analog-input";
    public final static String CHANNEL_TYPE_ANALOG_VALUE = "analog-value";
    public final static String CHANNEL_TYPE_ANALOG_OUTPUT = "analog-output";
    public final static String CHANNEL_TYPE_MULTI_STATE_INPUT = "multi-state-input";
    public final static String CHANNEL_TYPE_MULTI_STATE_INPUT_TEXT = "multi-state-input-text";

    public final static Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = ImmutableSet.of(BACNETIP_THING_TYPE,
            DEVICE_THING_TYPE);

    // device properties
    public final static String DEVICE_PROPERY_ADDRESS = "address";
    public final static String DEVICE_PROPERY_INSTANCE_NUMBER = "instanceNumber";

}
