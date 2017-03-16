package org.openhab.binding.bacnet.discovery;

import static org.openhab.binding.bacnet.BACnetBindingConstants.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.bacnet.handler.BACnetBridgeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.serotonin.bacnet4j.RemoteDevice;

public class BACnetDiscoveryService extends AbstractDiscoveryService {

    private static Logger logger = LoggerFactory.getLogger(BACnetDiscoveryService.class);

    private BACnetBridgeHandler bacnetBridgeHandler;
    private int discoveryTimeout;

    public BACnetDiscoveryService(BACnetBridgeHandler bacnetBridgeHandler, int discoveryTimeout)
            throws IllegalArgumentException {
        super(SUPPORTED_THING_TYPES_UIDS, discoveryTimeout);
        this.bacnetBridgeHandler = bacnetBridgeHandler;
        this.discoveryTimeout = discoveryTimeout;
    }

    @Override
    protected void startScan() {
        logger.debug("BACnet thing discovery started");
        List<RemoteDevice> remoteDevices = bacnetBridgeHandler.discoverRemoteDevices();
        for (RemoteDevice device : remoteDevices) {
            logger.debug("Device found: " + device.toString());
            processFoudDevice(device);
        }
        stopScan();
        logger.debug("BACnet device discovery process finished");
    }

    private void processFoudDevice(RemoteDevice device) {
        ThingUID thingUID = findDeviceThingUID(device);

        Map<String, Object> properties = new HashMap<>(2);
        properties.put(DEVICE_PROPERY_ADDRESS, device.getAddress().toString());
        properties.put(DEVICE_PROPERY_INSTANCE_NUMBER, device.getInstanceNumber());

        String displayLabel = device.getName() + " (" + device.getModelName() + ")";

        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(thingUID).withProperties(properties)
                .withBridge(bacnetBridgeHandler.getThing().getUID()).withLabel(displayLabel).build();

        thingDiscovered(discoveryResult);
    }

    private ThingUID findDeviceThingUID(RemoteDevice device) {
        String deviceId = Integer.toString(device.getInstanceNumber());
        return new ThingUID(DEVICE_THING_TYPE, bacnetBridgeHandler.getThing().getUID(), deviceId);
    }

}
