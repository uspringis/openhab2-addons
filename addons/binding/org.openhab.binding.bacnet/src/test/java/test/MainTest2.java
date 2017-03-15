package test;

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

public class MainTest2 {

    public static void main(String[] args) {

        Network network = new IpNetworkBuilder().build();
        DefaultTransport transport = new DefaultTransport(network);

        int deviceId = 1;
        LocalDevice localDevice = new LocalDevice(deviceId, transport);

        try {
            localDevice.initialize();
            RemoteDeviceDiscoverer rd = localDevice.startRemoteDeviceDiscovery();
            Thread.sleep(1000);
            rd.stop();
            for (RemoteDevice device : rd.getRemoteDevices()) {
                String result = "";
                if (device.getInstanceNumber() == 1001) {
                    SequenceOf<ObjectIdentifier> ol = RequestUtils.getObjectList(localDevice, device);
                    result = RequestUtils.getProperty(localDevice, device, ol.get(1), PropertyIdentifier.description)
                            .toString();
                }
                System.out.println("Device found: " + device.getModelName() + " " + device.getName() + " " + result
                        + " " + device.toString());
                // processFoudDevice(localDevice, device);

            }
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            localDevice.terminate();
        }

        System.out.println("THE END");

    }

    private static void processFoudDevice(LocalDevice localDevice, RemoteDevice remoteDevice) {
        try {
            System.out.println("RequestUtils.getObjectList START");
            SequenceOf<ObjectIdentifier> objlist;

            objlist = RequestUtils.getObjectList(localDevice, remoteDevice);

            System.out.println("RequestUtils.getObjectList END");

            System.out.println("Objec couny: " + objlist.getCount());

            for (ObjectIdentifier objectIdentifier : objlist) {

                String propertyList = "";
                String presentValue = "";

                try {
                    propertyList = RequestUtils
                            .getProperty(localDevice, remoteDevice, objectIdentifier, PropertyIdentifier.propertyList)
                            .toString();
                } catch (Exception e) {
                    // TODO: handle exception
                }

                try {
                    presentValue = RequestUtils
                            .getProperty(localDevice, remoteDevice, objectIdentifier, PropertyIdentifier.presentValue)
                            .toString();
                } catch (Exception e) {
                    // TODO: handle exception
                }

                if (objectIdentifier.getObjectType().equals(ObjectType.multiStateInput)) {
                    try {
                        presentValue = presentValue + "("
                                + RequestUtils.getProperty(localDevice, remoteDevice, objectIdentifier,
                                        PropertyIdentifier.stateText, Integer.parseInt(presentValue)).toString()
                                + ")";
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }

                System.out.println(objectIdentifier.toString() + ": "
                        + RequestUtils.getProperty(localDevice, remoteDevice, objectIdentifier,
                                PropertyIdentifier.objectName)
                        + " - " + propertyList + " - " + presentValue + " - " + RequestUtils.getProperty(localDevice,
                                remoteDevice, objectIdentifier, PropertyIdentifier.description));

            }
        } catch (BACnetException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

    }

}