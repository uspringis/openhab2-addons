/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.bacnet.internal;

import static org.openhab.binding.bacnet.BACnetBindingConstants.*;

import java.util.Hashtable;

import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.openhab.binding.bacnet.discovery.BACnetDiscoveryService;
import org.openhab.binding.bacnet.handler.BACnetBridgeHandler;
import org.openhab.binding.bacnet.handler.BACnetDeviceHandler;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link BACnetHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author ugis.springis@gmail.com - Initial contribution
 */
public class BACnetHandlerFactory extends BaseThingHandlerFactory {

    private static Logger logger = LoggerFactory.getLogger(BACnetHandlerFactory.class);

    private ServiceRegistration<?> discoveryServiceReg;

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected ThingHandler createHandler(Thing thing) {

        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (thingTypeUID.equals(BACNETIP_THING_TYPE)) {
            BACnetBridgeHandler bridgeHandler = new BACnetBridgeHandler((Bridge) thing);
            logger.debug("BACNETIP_THING_TYPE handler created");
            registerDeviceDiscoveryService(bridgeHandler, 5); // FIXME: use timeout from configuration
            return bridgeHandler;
        }
        if (thingTypeUID.equals(DEVICE_THING_TYPE)) {
            BACnetDeviceHandler deviceHandler = new BACnetDeviceHandler(thing);
            logger.debug("DEVICE_THING_TYPE handler created");
            return deviceHandler;
        }

        return null;
    }

    private void registerDeviceDiscoveryService(BACnetBridgeHandler bridgeHandler, int timeout) {
        BACnetDiscoveryService discoveryService = new BACnetDiscoveryService(bridgeHandler, timeout);
        discoveryServiceReg = bundleContext.registerService(DiscoveryService.class.getName(), discoveryService,
                new Hashtable<String, Object>());
        logger.debug("BACnetDiscoveryService created and registered");

    }

    @Override
    protected void removeHandler(ThingHandler thingHandler) {
        if (discoveryServiceReg != null && thingHandler.getThing().getThingTypeUID().equals(BACNETIP_THING_TYPE)) {
            discoveryServiceReg.unregister();
            discoveryServiceReg = null;
            logger.debug("BACnetDiscoveryService unregistered");
        }
        super.removeHandler(thingHandler);
    }

}
