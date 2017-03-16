package org.openhab.binding.bacnet.handler;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.thing.binding.builder.ChannelBuilder;
import org.eclipse.smarthome.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.npdu.Network;
import com.serotonin.bacnet4j.npdu.ip.IpNetworkBuilder;
import com.serotonin.bacnet4j.transport.DefaultTransport;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.util.RemoteDeviceDiscoverer;
import com.serotonin.bacnet4j.util.RequestUtils;

public class BACnetBridgeHandler extends BaseBridgeHandler {

    private static Logger logger = LoggerFactory.getLogger(BACnetBridgeHandler.class);

    private LocalDevice localDevice;

    public BACnetBridgeHandler(Bridge bridge) {
        super(bridge);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        logger.warn("This Bridge is read-only and does not handle commands");
    }

    @Override
    public void initialize() {
        logger.debug("Initializing BACnet/IP bridge handler.");

        disposeLocalDevice();

        Network network = new IpNetworkBuilder().build();
        DefaultTransport transport = new DefaultTransport(network);
        int deviceId = 1;
        localDevice = new LocalDevice(deviceId, transport);

        try {
            localDevice.initialize();
        } catch (Exception e) {
            logger.error("Error initializing local BACnet device!", e);
        }
        if (localDevice.isInitialized()) {
            logger.debug("Local device initialized sucessefully!");
            updateStatus(ThingStatus.ONLINE);
        } else {
            logger.error("Local device initialization failed!");
            updateStatus(ThingStatus.OFFLINE);
        }

    }

    @Override
    public void dispose() {
        logger.debug("Disposing BACnet/IP bridge handler.");
        disposeLocalDevice();
        updateStatus(ThingStatus.OFFLINE);
    }

    private void disposeLocalDevice() {
        if (localDevice != null) {
            if (localDevice.isInitialized()) {
                localDevice.terminate();
            }
            localDevice = null;
        }
    }

    public List<RemoteDevice> discoverRemoteDevices() {
        if (localDevice == null || !localDevice.isInitialized()) {
            logger.error("BACnet remote device discovery can not start - local device not initialized!");
            return new ArrayList<RemoteDevice>();
        } else {
            logger.debug("BACnet remote device discovery started");
            try {
                RemoteDeviceDiscoverer rd = localDevice.startRemoteDeviceDiscovery();
                Thread.sleep(3000L);
                rd.stop();
                logger.debug("BACnet remote device discovery completed, {} devices found",
                        rd.getRemoteDevices().size());
                return rd.getRemoteDevices();
            } catch (InterruptedException e) {
                logger.error("BACnet remote device discovery error", e);
            }
            return new ArrayList<RemoteDevice>();
        }
    }

    public List<Channel> getDeviceChannels(String deviceIntanceNumber, Thing deviceThing) {

        List<Channel> channelList = new ArrayList<Channel>();
        RemoteDevice remoteDevice = getRemoteDeviceByInstanceNumner(Integer.parseInt(deviceIntanceNumber));

        if (remoteDevice != null) {
            channelList = getRemoteDeviceCannels(remoteDevice, deviceThing);
        }

        return channelList;
    }

    private RemoteDevice getRemoteDeviceByInstanceNumner(int instanceNumber) {
        RemoteDevice remoteDevice = localDevice.getCachedRemoteDevice(instanceNumber);
        if (remoteDevice == null) {
            RemoteDeviceDiscoverer rd = localDevice.startRemoteDeviceDiscovery();
            try {
                Thread.sleep(3000L);
                remoteDevice = localDevice.getCachedRemoteDevice(instanceNumber);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                rd.stop();
            }
        }
        return remoteDevice;
    }

    private List<Channel> getRemoteDeviceCannels(RemoteDevice remoteDevice, Thing deviceThing) {
        List<Channel> channelList = new ArrayList<Channel>();
        try {
            SequenceOf<ObjectIdentifier> objlist;
            objlist = RequestUtils.getObjectList(localDevice, remoteDevice);

            for (ObjectIdentifier objectIdentifier : objlist) {

                List<Channel> objectChannels = new ArrayList<Channel>();

                String objectName = RequestUtils
                        .getProperty(localDevice, remoteDevice, objectIdentifier, PropertyIdentifier.objectName)
                        .toString();
                String description = RequestUtils
                        .getProperty(localDevice, remoteDevice, objectIdentifier, PropertyIdentifier.description)
                        .toString();

                if (objectIdentifier.getObjectType().equals(ObjectType.analogInput)) {
                    objectChannels.add(ChannelBuilder
                            .create(new ChannelUID(deviceThing.getUID(),
                                    Integer.toString(objectIdentifier.getInstanceNumber())), "Number")
                            .withDescription(description).withLabel(objectName).build());
                }

                if (objectIdentifier.getObjectType().equals(ObjectType.analogOutput)) {
                    objectChannels.add(ChannelBuilder
                            .create(new ChannelUID(deviceThing.getUID(),
                                    Integer.toString(objectIdentifier.getInstanceNumber())), "Number")
                            .withDescription(description).withLabel(objectName).build());
                }

                if (objectIdentifier.getObjectType().equals(ObjectType.analogValue)) {
                    objectChannels.add(ChannelBuilder
                            .create(new ChannelUID(deviceThing.getUID(),
                                    Integer.toString(objectIdentifier.getInstanceNumber())), "Number")
                            .withDescription(description).withLabel(objectName).build());
                }

                if (objectIdentifier.getObjectType().equals(ObjectType.multiStateInput)) {
                    objectChannels.add(ChannelBuilder
                            .create(new ChannelUID(deviceThing.getUID(),
                                    Integer.toString(objectIdentifier.getInstanceNumber())), "Number")
                            .withDescription(description).withLabel(objectName).build());
                    objectChannels.add(ChannelBuilder
                            .create(new ChannelUID(deviceThing.getUID(),
                                    Integer.toString(objectIdentifier.getInstanceNumber()) + "_text"), "String")
                            .withDescription(description).withLabel(objectName).build());
                }

                channelList.addAll(objectChannels);
            }

        } catch (BACnetException e) {
            logger.error("Error while reading remote device object list and preparing thing channels!", e);
        }
        return channelList;

    }

}
