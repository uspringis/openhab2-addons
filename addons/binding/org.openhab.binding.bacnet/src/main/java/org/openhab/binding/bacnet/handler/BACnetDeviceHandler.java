/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.bacnet.handler;

import static org.openhab.binding.bacnet.BACnetBindingConstants.DEVICE_PROPERY_INSTANCE_NUMBER;

import java.util.List;

import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.thing.binding.builder.ThingBuilder;
import org.eclipse.smarthome.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link BACnetDeviceHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author ugis.springis@gmail.com - Initial contribution
 */
public class BACnetDeviceHandler extends BaseThingHandler {

    private Logger logger = LoggerFactory.getLogger(BACnetDeviceHandler.class);

    public BACnetDeviceHandler(Thing thing) {
        super(thing);

    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // if (channelUID.getId().equals(CHANNEL_OBJECT_NAME)) {
        // // TODO: handle command
        //
        // // Note: if communication with thing fails for some reason,
        // // indicate that by setting the status with detail information
        // // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
        // // "Could not control device at IP address x.x.x.x");
        // }
    }

    @Override
    public void initialize() {

        List<Channel> channelList = ((BACnetBridgeHandler) this.getBridge().getHandler())
                .getDeviceChannels(thing.getProperties().get(DEVICE_PROPERY_INSTANCE_NUMBER), thing);
        ThingBuilder thingBuilder = editThing();
        thingBuilder.withChannels(channelList);
        if (channelList != null && !channelList.isEmpty()) {
            updateThing(thingBuilder.build());
            updateStatus(ThingStatus.ONLINE);
        } else {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "BACnet device with instance number " + thing.getProperties().get(DEVICE_PROPERY_INSTANCE_NUMBER)
                            + " not foud or objects can not be determined!");

        }

    }

    @Override
    public void thingUpdated(Thing thing) {
        logger.debug("thingUpdated received");
        super.thingUpdated(thing);
    }

}
