/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.bacnet.handler;

import static org.openhab.binding.bacnet.bacnetBindingConstants.CHANNEL_1;

import java.util.Set;

import org.code_house.bacnet4j.wrapper.api.BacNetClient;
import org.code_house.bacnet4j.wrapper.api.Device;
import org.code_house.bacnet4j.wrapper.api.DeviceDiscoveryListener;
import org.code_house.bacnet4j.wrapper.api.Property;
import org.code_house.bacnet4j.wrapper.ip.BacNetIpClient;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link bacnetHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author ugis.springis@gmail.com - Initial contribution
 */
public class bacnetHandler extends BaseThingHandler {

    private Logger logger = LoggerFactory.getLogger(bacnetHandler.class);

    public bacnetHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (channelUID.getId().equals(CHANNEL_1)) {
            // TODO: handle command

            // Note: if communication with thing fails for some reason,
            // indicate that by setting the status with detail information
            // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
            // "Could not control device at IP address x.x.x.x");
        }
    }

    @Override
    public void initialize() {
        // TODO: Initialize the thing. If done set status to ONLINE to indicate proper working.
        // Long running initialization should be done asynchronously in background.
        updateStatus(ThingStatus.ONLINE);

        // Note: When initialization can NOT be done set the status with more details for further
        // analysis. See also class ThingStatusDetail for all available status details.
        // Add a description to give user information to understand why thing does not work
        // as expected. E.g.
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
        // "Can not access device as username and/or password are invalid");
        logger.debug("Creating client");

        BacNetClient client = new BacNetIpClient("0.0.0.0", "255.255.255.255", 1111);
        client.start();
        logger.debug("Client started");

        Set<Device> devices = client.discoverDevices(new DeviceDiscoveryListener() {

            @Override
            public void deviceDiscovered(Device device) {
                logger.debug("Device found: " + device.toString());
            }
        }, 5000); // given number is timeout in millis
        logger.debug("Discovery completed");

        for (Device device : devices) {
            logger.debug("Device found: " + device.toString());
            for (Property p : client.getDeviceProperties(device)) {
                logger.debug("      Property: " + p.getName());
            }
        }

        client.stop();
        logger.debug("Client stoped");

    }

}
